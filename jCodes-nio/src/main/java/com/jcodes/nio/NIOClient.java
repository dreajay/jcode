package com.jcodes.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient {

	/* 缓冲区大小 */
	private static int BLOCK = 4096;
	private int count = 0;
	private Selector selector;
	private SocketChannel socketChannel;
	public NIOClient(String houstname, int port) throws IOException {
		// 打开socket通道
		socketChannel = SocketChannel.open();
		// 设置为非阻塞方式
		socketChannel.configureBlocking(false);
		// 打开选择器
		selector = Selector.open();
		// 注册连接服务端socket动作
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		// 连接
		socketChannel.connect(new InetSocketAddress(houstname, port));
	}

	/**
	 * 开始发送数据
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		while (true) {
			// 选择操作，阻塞式等待，直到至少有一个通道被选择，有可能返回多个，也有可能返回0个，当准备好操作的通道被更新状态了
			int selected = selector.select();
			if (selected > 0) {
				// 返回此选择器的已选择键集。
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					System.err.println("附加数据：" +selectionKey.attachment());
					handleKey(selectionKey);
					selectionKey.attach("我是同一个附加数据");
				}
				selectionKeys.clear();
			}
			
			if(count == 10) {
				break;
			}
			try {
				// 睡眠3秒
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
		socketChannel.close();
	}

	/**
	 * 处理选择器事件
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	private void handleKey(SelectionKey selectionKey) throws IOException {
		if (selectionKey.isConnectable()) {
			NIOUtils.connect(selectionKey);
			String sendMsg = "客户端连接";
			NIOUtils.write(selectionKey, sendMsg.getBytes());
			System.out.println("客户端发送数据：" + sendMsg);
		} else if (selectionKey.isReadable()) {
			byte[] dst = new byte[BLOCK];
			NIOUtils.read(selectionKey, dst);
			System.err.println("客户端接收数据：" + new String(dst, 0, dst.length));
		} else if (selectionKey.isWritable()) {
			String sendMsg = "Client--" + (count++);
			NIOUtils.write(selectionKey, sendMsg.getBytes());
			System.out.println("客户端发送数据：" + sendMsg);
		}
		
	}

	public static void main(String[] args) throws IOException {
		new NIOClient("localhost", 8888).start();
	}

}
