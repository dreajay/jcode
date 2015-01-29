package com.jcodes.mina.protocal;



public interface TcpRecvParser {
	
	/**
	 * 
	 * @param 
	 * @return 第一个返回值为true or false，表示该报文是否解析完毕
	 *         第二个返回值为 该报文实际的长度, bs.length 有可以能大于报文的实际长度
	 */
	public Object[] parseRevContent(byte[] bs);
}
