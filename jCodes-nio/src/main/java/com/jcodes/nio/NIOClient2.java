package com.jcodes.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetPermission;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NIOClient2 {
	
	/*标识数字*/
	private static int flag = 0;
	/*缓冲区大小*/
	private static int BLOCK = 4096;
	/*接受数据缓冲区*/
	private static ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);
	/*发送数据缓冲区*/
	private static ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
	/*服务器端地址*/
	private final static InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(
			"localhost", 8888);
	Selector selector;
	

	public static void main(String[] args) throws IOException {
		new NIOClient2().conn();
	}
	
	public void conn() throws IOException {
		// TODO Auto-generated method stub
		// 打开socket通道
		SocketChannel socketChannel = SocketChannel.open();
		// 设置为非阻塞方式
		socketChannel.configureBlocking(false);
		// 打开选择器
		selector = Selector.open();
		// 注册连接服务端socket动作
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		// 连接
		socketChannel.connect(SERVER_ADDRESS);
		// 分配缓冲区大小内存
		
		Set<SelectionKey> selectionKeys;
		Iterator<SelectionKey> iterator;
		SelectionKey selectionKey;
		SocketChannel client;
		String receiveText;
		String sendText;
		int count=0;
		int exit = 10;
		while (true) {
			//选择一组键，其相应的通道已为 I/O 操作准备就绪。
			//此方法执行处于阻塞模式的选择操作。
			selector.select();
			//返回此选择器的已选择键集。
			selectionKeys = selector.selectedKeys();
			//System.out.println(selectionKeys.size());
			iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				selectionKey = iterator.next();
				if (selectionKey.isConnectable()) {
					System.out.println("client connect");
					client = (SocketChannel) selectionKey.channel();
					// 判断此通道上是否正在进行连接操作。
					// 完成套接字通道的连接过程。
					if (client.isConnectionPending()) {
						client.finishConnect();
						System.out.println("完成连接!");
						sendbuffer.clear();
						sendbuffer.put("Hello,Server".getBytes());
						sendbuffer.flip();
						// 向通道中写数据
						client.write(sendbuffer);
					}
					client.register(selector, SelectionKey.OP_READ);
				} else if (selectionKey.isReadable()) {
					client = (SocketChannel) selectionKey.channel();
					//将缓冲区清空以备下次读取
					receivebuffer.clear();
					//读取服务器发送来的数据到缓冲区中，这里最好一直读直到返回-1
					count=client.read(receivebuffer);
					if(count>0){
						receiveText = new String( receivebuffer.array(),0,count);
						System.out.println("客户端接受服务器端数据--:"+receiveText);
						client.register(selector, SelectionKey.OP_WRITE);
					}

				} else if (selectionKey.isWritable()) {
					sendbuffer.clear();
					client = (SocketChannel) selectionKey.channel();
					sendText = "message from client--" + (flag++);
					sendbuffer.put(sendText.getBytes());
					 //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
					sendbuffer.flip();
					client.write(sendbuffer);
					System.out.println("客户端向服务器端发送数据--："+sendText);
					client.register(selector, SelectionKey.OP_READ);
				}
			}
			selectionKeys.clear();
		}
		
//		socketChannel.close();
	}
	
	/**
	 * 读取通道数据
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	public SocketChannel read(SelectionKey selectionKey) throws IOException {
		// 获得可读的通道。
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		// 配置为非阻塞
		channel.configureBlocking(false);
		// 创建缓冲区
		ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
		// 先清空缓冲区
		receivebuffer.clear();
		// 从通道中读取发送来的数据到缓冲区中
		int count = channel.read(receivebuffer);
		if (count > 0) {
			String receiveText = new String(receivebuffer.array(), 0, count);
			System.out.println("服务器端接受客户端数据--:" + receiveText);
		}
		// 把缓冲区位置复位到开头
		receivebuffer.flip();
		// 通道数据读完之后，注册可写事件到选择器，之后可以进行写操作
		channel.register(selector, SelectionKey.OP_WRITE);
		selectionKey.attach("aaaa");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("key", "val");// 普通数据
		data.put("receive", receivebuffer);
		// attach可以把对象附加在选择器中，后面可以获得
		selectionKey.attach(data);
		return channel;
	}
}
