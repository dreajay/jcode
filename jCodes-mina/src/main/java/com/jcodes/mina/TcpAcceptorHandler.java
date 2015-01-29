package com.jcodes.mina;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class TcpAcceptorHandler extends IoHandlerAdapter {
	
	public final static Logger logger = Logger.getLogger(TcpAcceptorHandler.class);
    
	
	public TcpAcceptorHandler() {
	}

	public void sessionCreated(IoSession session) throws Exception {
		// 该方法由NioProcessor线程来执行
		logger.debug("TcpAcceptor sessionCreated:" + session.getId());
	}

    public void sessionOpened(IoSession session) {
    	// ExecutorFilter会调用executor.execute(event)来执行当前语句，
    	// 由于会用到线程池，故执行线程与exceptionCaught、sessionClosed等不一定相同，
    	// 同理messageSent、messageReceived、exceptionCaught都会有这种情况
        logger.debug("TcpAcceptor sessionOpened:" + session.getId());
    }

    public void exceptionCaught(IoSession session, Throwable t)
            throws Exception {
    	logger.error("TcpAcceptor exceptionCaught:" + session.getId(), t);
    }

    public void sessionClosed(IoSession session) {
        logger.info("TcpAcceptor sessionClosed:" + session.getId() +" total " + session.getReadBytes() + " byte(s)");
    }

    public void sessionIdle(IoSession session, IdleStatus status) {
		logger.debug("TcpAcceptor sessionIdle:" + session.getId() +" status is " + status + ". ");
    }

    public void messageSent(IoSession session, Object message) throws Exception {
    	// 日志改换到messageReceived中，此处只是指示信息已发送完成，避免因为多线程问题导致日志信息不一致
		logger.debug("TcpAcceptor SENT:" + session.getId());
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
    	if (logger.isInfoEnabled())
			logger.info("TcpAcceptor RECEIVED:" + session.getId() +" \r\n" + new String((byte[])message));
    	byte[] ret = null;
    	try {
//    		执行业务逻辑返回数据
    		ret = doHandler((byte[])message);
    	} catch (Exception e) {
        	logger.error("TcpAcceptor InvokeFlow Error:" + session.getId(), e);
        	// 短连接出现异常，由于不会写数据，故需要在此处关闭连接
//        	if (this instanceof ShortAcceptorHandler)
        	session.close(true);
        	return;
    	}
    	if (ret != null && ret.length > 0) {
    		// 处理应答时，若连接closeing或closed情况。若不存在该语句，该类信息不会报错而是被mina忽略了
    		if (session.isClosing() || !session.isConnected()) {
        		logger.error("连接已断开，忽略发送数据：" + session.getId() +" \r\n" + new String(ret));
        		return;
    		}
    		if (logger.isInfoEnabled())
    			logger.info("TcpAcceptor SEND:" + session.getId() +" \r\n" + new String(ret));
    		session.write(ret);
    	}
    }
    
    protected byte[] doHandler(byte[] recv) {
    	return recv;
    }
    
}

