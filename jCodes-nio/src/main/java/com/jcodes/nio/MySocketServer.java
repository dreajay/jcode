package com.jcodes.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java NIO 聊天室服务端
 * 
 * @author
 * @version CreateTime：2010-12-1 下午05:12:11 Description：
 */
public class MySocketServer implements Runnable {

	private boolean running;

	private Selector selector;
	String writeMsg;
	StringBuffer sb = new StringBuffer();
	SelectionKey ssckey;

	public MySocketServer() {
		running = true;
	}

	public void init() {
		try {
			selector = Selector.open();
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ssc.socket().bind(new InetSocketAddress(2345));
			ssckey = ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("server is starting..." + new Date());
		} catch (IOException ex) {
			Logger.getLogger(MySocketServer.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void execute() {
		try {
			while (running) {
				int num = selector.select();
				if (num > 0) {
					Iterator<SelectionKey> it = selector.selectedKeys()
							.iterator();
					while (it.hasNext()) {
						SelectionKey key = it.next();
						it.remove();
						if (!key.isValid())
							continue;
						if (key.isAcceptable()) {
							System.out.println("isAcceptable");
							getConn(key);
						} else if (key.isReadable()) {
							System.out.println("isReadable");
							readMsg(key);
						}

						else if (key.isValid() && key.isWritable()) {
							if (writeMsg != null) {
								System.out.println("isWritable");
								writeMsg(key);
							}

						}

						else
							break;

					}

				}
				Thread.yield();
			}

		} catch (IOException ex) {
			Logger.getLogger(MySocketServer.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private void getConn(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		System.out.println("build connection :"
				+ sc.socket().getRemoteSocketAddress());
	}

	private void readMsg(SelectionKey key) throws IOException {
		sb.delete(0, sb.length());
		SocketChannel sc = (SocketChannel) key.channel();
		System.out.print(sc.socket().getRemoteSocketAddress() + " ");
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.clear();
		int len = 0;
		StringBuffer sb = new StringBuffer();
		while ((len = sc.read(buffer)) > 0) {
			buffer.flip();
			sb.append(new String(buffer.array(), 0, len));
		}
		if (sb.length() > 0)
			System.out.println("get from client:" + sb.toString());
		if (sb.toString().trim().toLowerCase().equals("quit")) {
			sc.write(ByteBuffer.wrap("BYE".getBytes()));
			System.out.println("client is closed "
					+ sc.socket().getRemoteSocketAddress());
			key.cancel();
			sc.close();
			sc.socket().close();

		} else {
			String toMsg = sc.socket().getRemoteSocketAddress() + "said:"
					+ sb.toString();
			System.out.println(toMsg);

			writeMsg = toMsg;

			/*
			 * Iterator<SelectionKey> it=key.selector().keys().iterator();
			 * 
			 * while(it.hasNext()){ SelectionKey skey=it.next();
			 * if(skey!=key&&skey!=ssckey){ SocketChannel client=(SocketChannel)
			 * skey.channel(); client.write(ByteBuffer.wrap(toMsg.getBytes()));
			 * }
			 * 
			 * }
			 */

			/*
			 * 
			 * key.attach(toMsg);
			 * key.interestOps(key.interestOps()|SelectionKey.OP_WRITE);
			 */
			Iterator<SelectionKey> it = key.selector().keys().iterator();

			while (it.hasNext()) {
				SelectionKey skey = it.next();
				if (skey != key && skey != ssckey) {
					if (skey.attachment() != null) {
						String str = (String) skey.attachment();
						skey.attach(str + toMsg);
					} else {
						skey.attach(toMsg);
					}
					skey.interestOps(skey.interestOps() | SelectionKey.OP_WRITE);
				}

			}
			selector.wakeup();// 可有可无

		}

	}

	public void run() {
		init();
		execute();
	}

	private void writeMsg(SelectionKey key) throws IOException {
		System.out.println("++++enter write+++");
		SocketChannel sc = (SocketChannel) key.channel();
		String str = (String) key.attachment();

		sc.write(ByteBuffer.wrap(str.getBytes()));
		key.interestOps(SelectionKey.OP_READ);
	}

	public static void main(String[] args) {
		MySocketServer server = new MySocketServer();
		new Thread(server).start();

	}
}