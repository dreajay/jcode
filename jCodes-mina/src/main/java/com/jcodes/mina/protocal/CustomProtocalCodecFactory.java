package com.jcodes.mina.protocal;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 * 此为接口，不为类，自定义扩展可以继承如DemuxingProtocolCodecFactory或其他类
 * 
 */
public interface CustomProtocalCodecFactory extends ProtocolCodecFactory {
	
	Map<String, Object> attributes = new HashMap<String, Object>();
	
	public void setAttributes(Map<String, Object> attributes);
	
}
