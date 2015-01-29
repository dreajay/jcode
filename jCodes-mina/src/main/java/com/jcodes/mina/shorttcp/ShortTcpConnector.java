package com.jcodes.mina.shorttcp;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.jcodes.mina.RecvPacketRuleCfg;
import com.jcodes.mina.TcpConnector;
import com.jcodes.mina.TcpConnectorHandler;
import com.jcodes.mina.protocal.ProtocolCodecFactoryFactory;
import com.jcodes.util.ExecutorUtils;

public class ShortTcpConnector extends TcpConnector {

	public final static Logger logger = Logger
			.getLogger(ShortTcpConnector.class);

	public ShortTcpConnector(RecvPacketRuleCfg rule, TcpConnectorHandler handler) {
		super(rule, handler);
	}
	
	public byte[] execute(byte[] message, Object... args) throws Exception {
		IoSession session = null;
		try {
			// 连接服务器
			session = connect();
			return doExecute(session, message, args);
		} finally {
			// 关闭连接
			if (session != null) {
				close(session);
				handler.removeReadFuture(session); // 此句最好放关闭session之后
			}
		}
	}

	protected String getConnectorName() {
		return "Tcp短连接";
	}

}
