package com.jcodes.nio;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOUtils {

	/**
	 * 接受请求
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	public static SocketChannel accept(SelectionKey selectionKey) throws IOException {
		// 返回之前创建的服务器通道
		ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
		// 接受到此通道套接字的连接，此方法返回的套接字通道（如果有）将处于阻塞模式。
		SocketChannel client = server.accept();
		// 配置为非阻塞
		client.configureBlocking(false);
		// 注册可读兴趣到选择器
		Selector selector = selectionKey.selector();
		client.register(selector, SelectionKey.OP_READ);
		return client;
	}

	/**
	 * 连接请求
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	public static SocketChannel connect(SelectionKey selectionKey) throws IOException {
		SocketChannel client = (SocketChannel) selectionKey.channel();
		// 判断此通道上是否正在进行连接操作。
		if (client.isConnectionPending()) {
			// 完成套接字通道的连接过程。
			client.finishConnect();
		}
		// 配置为非阻塞
		client.configureBlocking(false);
		Selector selector = selectionKey.selector();
		client.register(selector, SelectionKey.OP_WRITE);
		return client;
	}

	/**
	 * 读取通道数据
	 * 
	 * @param selectionKey
	 * @param dst
	 *            读取数据存放的目标地方
	 * @return
	 * @throws IOException
	 */
	public static SocketChannel read(SelectionKey selectionKey, byte[] dst) throws IOException {
		// 获得可读的通道。
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		// 配置为非阻塞
		channel.configureBlocking(false);
		// 创建缓冲区
		ByteBuffer receivebuffer = ByteBuffer.allocate(dst.length);
		// 从通道中读取发送来的数据到缓冲区中,一直读，直到返回-1
		int count = 0;
		int read = channel.read(receivebuffer);
		while (read > 0) {
			count += read;
			read = channel.read(receivebuffer);
		}
		receivebuffer.flip();
		// 把缓冲区数据传输到目标数组中
		receivebuffer.get(dst, 0, count);
		Selector selector = selectionKey.selector();
		channel.register(selector, SelectionKey.OP_WRITE);
		return channel;
	}

	/**
	 * 向通道写数据
	 * 
	 * @param selectionKey
	 * @param data
	 * @throws IOException
	 */
	public static SocketChannel write(SelectionKey selectionKey, byte[] data) throws IOException {
		// 获得可读的通道。
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		// 配置为非阻塞
		channel.configureBlocking(false);
		// 创建缓冲区
		ByteBuffer sendbuffer = ByteBuffer.allocate(data.length);
		// 把数据放到缓冲区
		sendbuffer.put(data);
		// 把缓冲区复位到开始位置，否则缓冲区的数据写不到通道中
		sendbuffer.flip();
		// 向通道中写数据
		channel.write(sendbuffer);
		// 通道数据写完之后，注册可读事件到选择器，之后可以进行读操作
		Selector selector = selectionKey.selector();
		channel.register(selector, SelectionKey.OP_READ);
		return channel;
	}

	/**
	 * 向通道写数据，解决网络慢速练练问题
	 * 
	 * @param selectionKey
	 * @param data
	 * @throws IOException
	 */
	public static SocketChannel write2(SelectionKey selectionKey, byte[] data) throws IOException {
		// 获得可读的通道。
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		// 配置为非阻塞
		channel.configureBlocking(false);
		// 创建缓冲区
		ByteBuffer sendbuffer = ByteBuffer.allocate(data.length);
		// 把数据放到缓冲区
		sendbuffer.put(data);
		// 把缓冲区复位到开始位置，否则缓冲区的数据写不到通道中
		sendbuffer.flip();
		// 通道数据写完之后，注册可读事件到选择器，之后可以进行读操作
		Selector selector = selectionKey.selector();
		
		// 向通道中写数据
		channel.write(sendbuffer);
		while (sendbuffer.hasRemaining()) {
			int len = channel.write(sendbuffer);
			if (len < 0) {
				throw new EOFException();
			}
			if (len == 0) {
				selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
				selector.wakeup();
				break;
			}
		}

		channel.register(selector, SelectionKey.OP_READ);
		return channel;
	}

	/**
	 * 向通道写数据，解决网络慢速练练问题
	 * 
	 * @param selectionKey
	 * @param data
	 * @throws IOException
	 */
	public static SocketChannel write3(SelectionKey selectionKey, byte[] data) throws IOException {
		// 获得可读的通道。
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		// 配置为非阻塞
		channel.configureBlocking(false);
		// 创建缓冲区
		ByteBuffer sendbuffer = ByteBuffer.allocate(data.length);
		// 把数据放到缓冲区
		sendbuffer.put(data);
		// 把缓冲区复位到开始位置，否则缓冲区的数据写不到通道中
		sendbuffer.flip();
		// 通道数据写完之后，注册可读事件到选择器，之后可以进行读操作
		Selector selector = selectionKey.selector();
		
		// 向通道中写数据
		channel.write(sendbuffer);
		while (sendbuffer.hasRemaining()) {
			int len = channel.write(sendbuffer);
			if (len < 0) {
				throw new EOFException();
			}
			if (len == 0) {
				selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
				selector.wakeup();
				break;
			}
		}

		channel.register(selector, SelectionKey.OP_READ);
		return channel;
	}
	
	

}