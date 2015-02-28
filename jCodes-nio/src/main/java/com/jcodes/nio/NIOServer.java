package com.jcodes.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
	/* 缓冲区大小 */
	public static final int BLOCK = 4096;
	private int count = 0;
	private Selector selector;
	private boolean started;

	public NIOServer(int port) throws IOException {
		// 打开服务器套接字通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// 服务器配置为非阻塞
		serverSocketChannel.configureBlocking(false);
		// 检索与此通道关联的服务器套接字
		ServerSocket serverSocket = serverSocketChannel.socket();
		// 进行服务的绑定
		serverSocket.bind(new InetSocketAddress(port));
		// 通过open()方法找到Selector
		selector = Selector.open();
		// 把serverSocketChannel通道注册可接受连接事件到选择器，等待连接
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 停止监听
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {
		started = false;
	}
	
	/**
	 * 开启监听
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		started = true;
		while (started) {
			// 选择操作，阻塞式等待，直到至少有一个通道被选择，有可能返回多个，也有可能返回0个，当准备好操作的通道被更新状态了
			int selected = selector.select();
			if(selected > 0) {
				// 返回此选择器的已选择键集。
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					handleKey(selectionKey);
				}
				// 处理过后要手动移除
				selectionKeys.clear();
			}
			
		}
	}

	/**
	 * 处理选择器事件
	 * @param selectionKey
	 * @throws IOException
	 */
	private void handleKey(SelectionKey selectionKey) throws IOException {
		if (selectionKey.isAcceptable()) { // 该选择器的通道已准备接受连接
			NIOUtils.accept(selectionKey);
		} else if (selectionKey.isReadable()) { // 该选择器的通道是可读的
			byte[] receive = new byte[BLOCK];
			NIOUtils.read(selectionKey, receive);
			System.err.println("服务器收到数据：" + new String(receive));
		} else if (selectionKey.isWritable()) { // 该选择器的通道是可写的
			String sendMsg = "Server--" + count++;
			NIOUtils.write(selectionKey, sendMsg.getBytes());
			System.out.println("服务器发送数据：" + sendMsg);
		}
	}


	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NIOServer server = new NIOServer(8888);
		System.out.println("服务启动");
		server.start();
	}
}