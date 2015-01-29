package com.jcodes.mina.protocal;

import org.apache.mina.filter.codec.ProtocolCodecFactory;

import com.jcodes.mina.RecvPacketRuleCfg;


/**
 * 协议解码工厂实例工厂类
 * 
 */
public class ProtocolCodecFactoryFactory implements TcpConstants.RecvPacketRuleConstants {

	public static ProtocolCodecFactory getInstance(RecvPacketRuleCfg rule) {
		if (TYPE_LENGTHIDENTIFY.equals(rule
				.getType())) {
			LengthIdentifyProtocalCodecFactory ret = new LengthIdentifyProtocalCodecFactory();
			ret.setLenFieldType((String) rule.get("valType"));
			ret.setLenFillType((String) rule.get("fillType"));
			ret.setLenFillChar((String) rule.get("fillChar"));
			ret.setLenFillLocation((String) rule.get("fillLocation"));
			ret.setOffset((Integer) rule.get("offset"));
			ret.setLenFieldLen((Integer) rule.get("len"));
			ret.setReduce((Integer) rule.get("reduce"));
			return ret;
		}
		if (TYPE_FIXLENGTH.equals(rule.getType())) {
			FixLengthProtocalCodecFactory ret = new FixLengthProtocalCodecFactory();
			ret.setFixLength((Integer) rule.get("len"));
			return ret;
		}
		if (TYPE_ENDCHAR.equals(rule.getType())) {
			EndCharProtocalCodecFactory ret = new EndCharProtocalCodecFactory();
			ret.setEndChar((String) rule.get("endChar"));
			ret.setEscapeChar((String) rule.get("escapeChar"));
			return ret;
		}
		if (TYPE_CUSTOM.equals(rule.getType())) {
			CustomProtocalCodecFactory ret = null;
			String clazz = (String) rule.get("class");
			try {
				ret = (CustomProtocalCodecFactory) Class
						.forName(clazz).newInstance();
				ret.setAttributes(rule.getAttributes());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		}
		
		if(TYPE_CUSTOMEX.equals(rule.getType())){
			CustomexProtocalCodecFactory ret = new CustomexProtocalCodecFactory();
			ret.setHandler((String)rule.get("handler"));
			return ret;
		}
		return null;
	}
}
