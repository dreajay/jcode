package com.jcodes.mina.shorttcp;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;

import com.jcodes.mina.RecvPacketRuleCfg;
import com.jcodes.mina.TcpAcceptor;
import com.jcodes.mina.TcpAcceptorHandler;
import com.jcodes.mina.protocal.ProtocolCodecFactoryFactory;

/**
 * Tcp短连接接入适配器
 * 
 */
public class ShortTcpAcceptor extends TcpAcceptor{
	

	public ShortTcpAcceptor(RecvPacketRuleCfg rule, TcpAcceptorHandler handler) {
		super(rule, handler);
	}

	protected void buildFilterChain() {
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		filterChain.addLast("codec",new ProtocolCodecFilter(ProtocolCodecFactoryFactory.getInstance(rule)));
		IoEventType[] DEFAULT_EVENT_SET = new IoEventType[] {
		        IoEventType.MESSAGE_RECEIVED
		    };
		// businessPool用来处理DEFAULT_EVENT_SET事件，这里只处理获取
		filterChain.addLast("businessPool", new ExecutorFilter(businessExecutor, DEFAULT_EVENT_SET));
	}
	
	protected String getAcceptorName() {
		return "Tcp短连接";
	}
	
}
