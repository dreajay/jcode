package com.jcodes.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class TcpConnectorHandler extends IoHandlerAdapter {
	
	private final static Logger logger = Logger.getLogger(TcpConnectorHandler.class);
	
	/**handlers的目的是为了处理多线程并发，使用一个handler，但对于每个连接都有一个ReadFuture*/
	protected Map<IoSession, ReadFuture> handlers = //Collections.synchronizedMap(new HashMap<IoSession, ReadFuture>());
													new ConcurrentHashMap<IoSession, ReadFuture>();

	
	public TcpConnectorHandler() {
	}
	
	public void sessionCreated(IoSession session) throws Exception {
		logger.debug("TcpConnector sessionCreated:" + session.getId());
	}

    public void sessionOpened(IoSession session) {
        logger.debug("TcpConnector sessionOpened:" + session.getId());
    }

    // 异常捕获后，会自动让当前future线程执行完
    public void exceptionCaught(IoSession session, Throwable t){
    	logger.error("TcpConnector exceptionCaught:" + session.getId(), t);
    	ReadFuture future = getReadFuture(session);
    	if (future != null)
    		future.setException(new Exception(t));
        session.close(true);
    }

    public void sessionClosed(IoSession session) {
        logger.info("TcpConnector sessionClosed:" + session.getId() +" total " + session.getReadBytes() + " byte(s)");
        ReadFuture future = null;
    	// 此处有可能远程主机已经关闭连接，但socket客户端未收到数据，故需要在此处抛出异常
    	future = getReadFuture(session);
    	if (future != null)
    		future.setException(new Exception("远程服务器Tcp已关闭连接"));
    }

    public void sessionIdle(IoSession session, IdleStatus status) {
		logger.debug("TcpConnector sessionIdle:" + session.getId() +" the status is " + status + ". ");
    }

    public void messageSent(IoSession session, Object message) throws Exception {
//    	if (logger.isInfoEnabled())
//			logger.info("TcpConnector SENT:\r\n" + CommHelper.messageToString(cfg, message));
    	logger.debug("TcpConnector SENT:" + session.getId());
    }

    public void messageReceived(IoSession session, Object message) {
    	// 日志放TcpConnector中，避免因为connector使用线程池而无法定位日志属于哪个接入线程
//    	if (logger.isInfoEnabled())
//			logger.info("TcpConnector RECEIVED:\r\n" + CommHelper.messageToString(cfg, message));
    	logger.debug("TcpConnector RECEIVED:" + session.getId());
    	ReadFuture future = getReadFuture(session);
    	if (future != null)
    		future.setRead(message);
    }
    
    protected ReadFuture getReadFuture(IoSession s) {
    	return handlers.get(s);
    }
    
    public void addReadFuture(IoSession s, ReadFuture f) {
    	handlers.put(s, f);
    }
    
    public void removeReadFuture(IoSession s) {
    	handlers.remove(s);
    }
    
}
