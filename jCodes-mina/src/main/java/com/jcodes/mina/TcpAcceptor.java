package com.jcodes.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.jcodes.util.ExecutorUtils;

/**
 * Tcp接入适配器抽象父类
 * 
 */
abstract public class TcpAcceptor {
	
	protected NioSocketAcceptor acceptor;
	protected RecvPacketRuleCfg rule;
	protected TcpAcceptorHandler handler;
	protected int coreSize = Runtime.getRuntime().availableProcessors() + 1;
	/**接入工作线程池*/
	protected ExecutorService acceptorExecutor;
	/**IO处理线程池*/
	protected ExecutorService ioExecutor;
	/**业务处理线程池*/
	protected ExecutorService businessExecutor;
	
	public TcpAcceptor(RecvPacketRuleCfg rule,TcpAcceptorHandler handler) {
		this.handler = handler;;
		this.rule = rule;
	}
	
	protected void buildExecutors() {
		// 此处自己创建三种线程执行器，而不使用缺省，便于日志MDC注入接入信息
		acceptorExecutor = Executors.newCachedThreadPool();
		ioExecutor = Executors.newCachedThreadPool();
		businessExecutor = Executors.newCachedThreadPool();
	}
	
	protected void start() throws Exception{
		
		buildExecutors();
		IoProcessor<NioSession> processor = new SimpleIoProcessorPool<NioSession>(NioProcessor.class, ioExecutor, coreSize);
		acceptor = new NioSocketAcceptor(acceptorExecutor, processor);
//		acceptor.setBacklog(cfg.getBacklog());
		buildFilterChain();
		acceptor.setHandler(handler);
		try {
			List<SocketAddress> address = new ArrayList<SocketAddress>();
			//可添加多个
			address.add(new InetSocketAddress((String)rule.get("ip"), (Integer)rule.get("port")));
			acceptor.bind(address);
		} catch (IOException e) {
			stop();
			throw e;
		}
		
	}
	
	
	protected void stop() {
		// 停止该端口接受连接请求 FIXME 由于executor是外部传入，故dispose(boolean)参数无效
		// 经测试当调用acceptor.dispose()时，内部调用的selector.wakeup()将中断select()，端口由此被取消绑定
		// 故原先未处理好的请求当处理完成时由于网络原因已不能将应答发出去，故需要先停止executor，再dispose
		// 停止接受流程执行请求，在执行完当前所有正在运行流程后关闭执行线程池
		ExecutorUtils.shutdownAndAwaitTermination(businessExecutor);
		ExecutorUtils.shutdownAndAwaitTermination(ioExecutor);
		acceptor.dispose();
		ExecutorUtils.shutdownNowAndAwaitTermination(acceptorExecutor);
		
	}
	
	/**
	 * 构建IO请求处理过滤链
	 */
	abstract protected void buildFilterChain();
	

}
