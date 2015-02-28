package com.jcodes.nio.simulateQQ;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Server implements Runnable {

	// 选择器
	private Selector selector;

	// 选择key
	private SelectionKey sscKey;

	// 服务器开关
	private boolean isOpen;

	// 用户集合
	private List<Users> users;

	// 用户上线列表
	private Vector<String> userNames;

	public Server(int port) {
		isOpen = true;
		users = UserDao.getUsers();
		userNames = new Vector<String>();
		init(port);
	}

	@Override
	public void run() {
		try {
			while (isOpen) {
				// 接收信息的数量
				int result = selector.select();
				if (result > 0) {
					for (Iterator<SelectionKey> iterator = selector
							.selectedKeys().iterator(); iterator.hasNext();) {
						SelectionKey key = (SelectionKey) iterator.next();
						iterator.remove();
						// 判断是否是接收状态
						if (key.isAcceptable()) {
							System.out.println("==========客户端开启==========");
							getConn(key);
						}
						// 判断是否是读取状态
						else if (key.isReadable()) {
							System.out.println("=============读取=============");
							ReadMsg(key);
						}
						// 判断是否是写入状态
						else if (key.isWritable()) {
							System.out.println("=============写入=============");
							WriteMsg(key);
						}

					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 初始化服务器
	private void init(int port) {
		try {
			// 开启选择器
			selector = Selector.open();
			// 开启ServerSocket
			ServerSocketChannel ssc = ServerSocketChannel.open();
			// 设置非阻塞模式
			ssc.configureBlocking(false);
			// 设置端口
			ssc.socket().bind(new InetSocketAddress(port));
			// 注册到选择器里并设置为接收状态
			sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("==========开启服务器==========");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取连接
	private void getConn(SelectionKey key) throws IOException {
		// 获取ServerSocket
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		// 设置Socket
		SocketChannel sc = ssc.accept();
		// 设置非阻塞模式
		sc.configureBlocking(false);
		// 注册到选择器里并设置为读取状态
		sc.register(selector, SelectionKey.OP_READ);
	}

	// 读取信息
	private void ReadMsg(SelectionKey key) throws IOException {
		// 获取到Socket
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
		buffer.clear();
		StringBuffer sb = new StringBuffer();
		// 获取字节长度
		int count = sc.read(buffer);
		if (count > 0) {
			buffer.flip();
			sb.append(new String(buffer.array(), 0, count));
		}
		Object obj = (Object) sb.toString();
		if (obj.toString().indexOf("-") != -1) {
			// 获取用户名
			String userName = obj.toString().substring(0,
					obj.toString().indexOf("-"));
			// 获取用户密码
			String userPass = obj.toString().substring(
					obj.toString().indexOf("-") + 1);

			boolean isTrue = false;
			// 判断用户是否存在
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getUserName().equals(userName)
						&& users.get(i).getUserPass().equals(userPass)) {
					System.out.println("========" + userName + "登录成功========");
					isTrue = true;
					userNames.addElement(userName);
					KeyAttach(key, "true");
					break;
				}
				isTrue = false;
			}

			// 用户不存在
			if (!isTrue) {
				System.out.println("========" + userName + "登录失败========");
				KeyAttach(key, "false");
			}
		} else if (obj.toString().equals("open")) {
			System.out.println("=========开启聊天窗口=========");
			// 给都有的用户返回用户列表
			AllKeysAttach(key, userNames);
		} else if (obj.toString().indexOf("exit_") != -1) {
			String userName = obj.toString().substring(5);
			userNames.removeElement(userName);
			System.out.println("========" + userName + "退出窗体========");
			KeyAttach(key, "close");
			OtherKeysAttach(key, userNames);
		} else {
			// 获取用户名
			String userName = obj.toString().substring(0,
					obj.toString().indexOf("^"));
			// 获取信息
			String mess = obj.toString().substring(
					obj.toString().indexOf("^") + 1);
			// 获取发信时间
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			String dateTime = dateFormat.format(new Date());
			// 设置信息
			String mss = userName + " " + dateTime + "\n" + mess + "\n";
			// 给都有的用户返回聊天信息
			AllKeysAttach(key, mss);
		}

	}

	// 所有client改成写入状态
	private void AllKeysAttach(SelectionKey key, Object obj) {
		for (Iterator<SelectionKey> iterator = key.selector().keys().iterator(); iterator
				.hasNext();) {
			SelectionKey selKey = (SelectionKey) iterator.next();
			// 判断不是Server key;
			if (selKey != sscKey) {
				selKey.attach(obj);
				// 把其他client改成可写状态
				selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
			}
		}
	}

	// 把其他客户改成写入状态
	private void OtherKeysAttach(SelectionKey key, Object obj) {
		for (Iterator<SelectionKey> iterator = key.selector().keys().iterator(); iterator
				.hasNext();) {
			SelectionKey selKey = (SelectionKey) iterator.next();
			// 判断不是本生client key和Server key;
			if (selKey != sscKey && selKey != key) {
				selKey.attach(obj);
				// 把其他client改成可写状态
				selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
			}
		}
	}

	// 自身改成写入状态
	private void KeyAttach(SelectionKey key, Object obj) {
		key.attach(obj);
		key.interestOps(SelectionKey.OP_WRITE);
	}

	// 发送信息
	private void WriteMsg(SelectionKey key) throws IOException {
		// 获取到Socket
		SocketChannel sc = (SocketChannel) key.channel();
		// 获取附属值
		Object obj = key.attachment();
		// 把附属值设为空
		key.attach("");
		// 发送信息
		sc.write(ByteBuffer.wrap(obj.toString().getBytes()));
		if (obj.toString().equals("close") || obj.toString().equals("false")) {
			key.cancel();
			sc.socket().close();
			sc.close();
			System.out.println("==========客户端关闭==========");
			return;
		}
		// 设置为读取状态
		key.interestOps(SelectionKey.OP_READ);
	}

	public static void main(String[] args) {
		Server server = new Server(8001);
		new Thread(server).start();
	}
}