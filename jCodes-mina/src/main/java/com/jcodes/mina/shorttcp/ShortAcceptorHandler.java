package com.jcodes.mina.shorttcp;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.jcodes.mina.TcpAcceptorHandler;


public class ShortAcceptorHandler extends TcpAcceptorHandler {
	
	public ShortAcceptorHandler() {
	}

    public void sessionIdle(IoSession session, IdleStatus status) {
		super.sessionIdle(session, status);
		// 连接空闲时关闭连接
        session.close(true);
    }
    
    public void exceptionCaught(IoSession session, Throwable t)
            throws Exception {
    	super.exceptionCaught(session, t);
    	// 不管得到什么异常均关闭连接
        session.close(true);
    }

    public void messageSent(IoSession session, Object message) throws Exception {
    	super.messageSent(session, message);
    	// 发送完数据后关闭连接
        session.close(true);
    }
}
