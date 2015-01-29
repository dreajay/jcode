package com.jcodes.mina;

import com.jcodes.mina.protocal.TcpConstants;
import com.jcodes.mina.shorttcp.ShortAcceptorHandler;
import com.jcodes.mina.shorttcp.ShortConnectorHandler;
import com.jcodes.mina.shorttcp.ShortTcpAcceptor;
import com.jcodes.mina.shorttcp.ShortTcpConnector;

public class MainServer {
	public static void main(String[] arbs) throws Exception {
		startServer();
		startClient();
	}

	public static void startServer() throws Exception {
		RecvPacketRuleCfg rule = getRecvPacketRuleOfFixLength();
		rule.set("ip", "127.0.0.1");
		rule.set("port", 10001);
		TcpAcceptor acceptor = new ShortTcpAcceptor(rule,
				new ShortAcceptorHandler());
		acceptor.start();
		System.out.println("服务器启动成功...");
	}
	
	public static void startClient() throws Exception {
		RecvPacketRuleCfg rule = getRecvPacketRuleOfFixLength();
		rule.set("ip", "127.0.0.1");
		rule.set("port", 10001);
		rule.set("timeout", 100000);
		
		ShortTcpConnector connector = new ShortTcpConnector(rule,
				new ShortConnectorHandler());
		connector.start();
		byte[] message = "$$$000008abcdefgh__".getBytes();
		connector.execute(message, new Object[0]);
		
	}
	
	/**
	 * 定长报文<br>
	 * 报文例子："12345"，长度为5的定长报文
	 * 
	 * @return
	 */
	public static RecvPacketRuleCfg getRecvPacketRuleOfFixLength() {
		RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
		rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_FIXLENGTH);
		rule.set("len", 5);// 报文长度
		return rule;
	}

	/**
	 * 长度标示报文 <br>
	 * 报文例子："$$$000008abcdefgh__"<br>
	 * 
	 * 长度字段值的类型valType为char2，长度字段所在位置offset为3，长度为6，填充为0，所以
	 * 000008为报文长度，在获取报文长度后需要再读取的长度为8 -（-2）=10 所以整个的报文为abcdefgh__，8个字母加上2个下划线。
	 * 
	 * @return
	 */
	public static RecvPacketRuleCfg getRecvPacketRuleOfLengthIdentify() {
		RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
		rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_LENGTHIDENTIFY);
		rule.set("valType", TcpConstants.LengthIdentifyConstants.VALTYPE_CHARS);// 长度字段值的类型
		rule.set("fillType", TcpConstants.LengthIdentifyConstants.FILLTYPE_CHAR);// 填充方式
		rule.set("fillChar", "0");// 填充字符
		rule.set("fillLocation",
				TcpConstants.LengthIdentifyConstants.FILLLOCATION_LEFT);// 填充方向
		rule.set("offset", 3);// 长度字段所在位置
		rule.set("len", 6);// 长度字段的长度
		rule.set("reduce", -2);// 抛弃字节数，长度字段值-reduce为需接包长度
		return rule;
	}

	/**
	 * 分隔符报文 <br>
	 * 报文例子："00000008\#cdefghi#", 第一个#号前面有转义，所以会被跳过
	 * 
	 * @return
	 */
	public static RecvPacketRuleCfg getRecvPacketRuleOfEndChar() {
		RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
		rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_ENDCHAR);
		rule.set("endChar", "#");// 分隔符
		rule.set("escapeChar", "\\");// 转义字符
		return rule;
	}

}
