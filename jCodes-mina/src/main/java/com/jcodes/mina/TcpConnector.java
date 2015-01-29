package com.jcodes.mina;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultReadFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.jcodes.mina.protocal.ProtocolCodecFactoryFactory;
import com.jcodes.mina.shorttcp.ShortConnectorHandler;
import com.jcodes.util.ExecutorUtils;


/**
 * Tcp连接器父类
 * 
 */
abstract public class TcpConnector  {
	
	private final static Logger logger = Logger.getLogger(TcpConnector.class);

	protected NioSocketConnector connector;
	protected TcpConnectorHandler handler;
	protected int coreSize = Runtime.getRuntime().availableProcessors() + 1;
	/**工作线程池*/
	protected ExecutorService connectorExecutor;
	/**IO处理线程池*/
	protected ExecutorService ioExecutor;
	protected RecvPacketRuleCfg rule;
	
	public TcpConnector(RecvPacketRuleCfg rule, TcpConnectorHandler handler) {
		this.rule = rule;
		this.handler = handler;
	}
	
	protected void buildExecutors() {
		// 此处自己创建三种线程执行器，而不使用缺省，便于日志MDC注入接入信息
		connectorExecutor = new MyThreadPoolExecutor(1);
		ioExecutor = new MyThreadPoolExecutor(coreSize);
	}
	
	
	protected byte[] doExecute(IoSession session, byte[] message, Object...args) throws Exception {
		DefaultReadFuture readFuture = new DefaultReadFuture(session);
		handler.addReadFuture(session, readFuture);
		// 发送请求包
		sendRequest(session, message);
		// 读取应答包
//		ReadFuture readFuture = session.read();
		int timeout = args.length > 0 ? (Integer)args[0] : (Integer)rule.get("timeout")!=null?(Integer)rule.get("timeout"):10000;
		return recvResponse(readFuture, timeout);
	}
	
	protected void sendRequest(IoSession session, byte[] message) {
		if (logger.isInfoEnabled())
			logger.info("TcpConnector SEND:\r\n" + new String(message));
		WriteFuture future = session.write(message);
//		future.awaitUninterruptibly(/*10*1000L*/);
//		if (!future.isWritten())
//			throw new CommunicateException("往远程Tcp服务器写数据失败");
	}

	protected byte[] recvResponse(ReadFuture readFuture, int timeout) throws Exception  {
		// 读取等待超时
		boolean b = readFuture.awaitUninterruptibly(timeout);
		if (!b)
			throw new Exception("接收远程Tcp服务器数据超时");
		if (!readFuture.isRead()) {
			if (readFuture.getException() != null)
				throw new Exception("接收远程Tcp服务器数据异常", readFuture.getException());
			throw new Exception("接收远程Tcp服务器数据异常");
		}
		
		Object ret = readFuture.getMessage();
		if (logger.isInfoEnabled())
			logger.info("TcpConnector RECEIVED:\r\n" + new String((byte[])ret));
		return (byte[]) ret;
	}
	
	public IoSession connect() throws Exception {
		ConnectFuture future = connector.connect(new InetSocketAddress((String)rule.get("ip"), (Integer)rule.get("port")));
		future.awaitUninterruptibly();
		/*boolean b = future.awaitUninterruptibly(cfg.getConTimeout());
		// 若返回值b==true，则表示设置了ConnectFuture的value，即调用了ConnectFuture的setException、setSession或cancel方法
		if (!b)
			throw new CommunicateException("连接远程Tcp服务器超时");*/
		
		if (!future.isConnected())
			// 即getValue() instanceof IoSession == false，也就是说出现异常或Canceled
			throw new Exception("与远程Tcp服务器建立连接失败：超时或异常",
					future.getException());
		return future.getSession();
	}
	
	protected void close(IoSession session){
		session.close(true);
	}
	
	public void start() throws Exception {
		buildExecutors();
		IoProcessor<NioSession> processor = new SimpleIoProcessorPool<NioSession>(
				NioProcessor.class, ioExecutor, coreSize);
		connector = new NioSocketConnector(connectorExecutor, processor);
		connector.setConnectTimeoutMillis((Integer) rule.get("timeout")); // 设置连接超时。见AbstractPollingIoConnector.processTimedOutSessions()与ConnectionRequest类
		// connector.getSessionConfig().setUseReadOperation(true); //
		// 亦可使用该方式实现同步发送并接收数据，这样无须设置Handler，通过session.read()获取
		handler = new ShortConnectorHandler();
		connector.setHandler(handler);
		DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
		filterChain.addLast("codec", new ProtocolCodecFilter(
				ProtocolCodecFactoryFactory.getInstance(rule)));
	}

	public void stop() throws Exception {
		ExecutorUtils.shutdownAndAwaitTermination(ioExecutor);
		connector.dispose();
		ExecutorUtils.shutdownAndAwaitTermination(connectorExecutor);
	}
	
	abstract protected String getConnectorName();
	
	class MyThreadPoolExecutor extends ThreadPoolExecutor {
		public MyThreadPoolExecutor(int corePoolSize) {
			super(corePoolSize, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		}
		protected void beforeExecute(Thread t, Runnable r) {
			String GROUP_KEY = "GROUP_KEY";
			Object group = MDC.get(GROUP_KEY);
			// 将group名称绑定到当前log4j的线程上下文中
			Hashtable<?, ?> ht = MDC.getContext();
			if (ht != null)
				ht.clear();
			if (group != null)
				MDC.put(GROUP_KEY, group);
		}
	}

}
