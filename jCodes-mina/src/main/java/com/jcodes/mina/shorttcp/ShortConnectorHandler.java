package com.jcodes.mina.shorttcp;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.jcodes.mina.TcpConnectorHandler;


/**
 * 此类主要负责读取数据，供connector返回
 */
public class ShortConnectorHandler extends TcpConnectorHandler {
	
	public ShortConnectorHandler() {
	}

	public void sessionIdle(IoSession session, IdleStatus status) {
		super.sessionIdle(session, status);
    	ReadFuture future = getReadFuture(session);
		if (future != null)
	        future.setException(new Exception("远程服务器SOCKET通讯连接空闲：" + status));
        session.close(true);
    }
	
	 public void messageSent(IoSession session, Object message) throws Exception {
    	super.messageSent(session, message);
    }
	
	public void messageReceived(IoSession session, Object message) {
    	super.messageReceived(session, message);
    	session.close(true);
    }
}
