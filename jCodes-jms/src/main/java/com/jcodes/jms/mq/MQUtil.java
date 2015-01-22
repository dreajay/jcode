package com.jcodes.jms.mq;

import java.util.ArrayList;
import java.util.Collection;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.ibm.mq.jms.MQConnection;
import com.ibm.mq.jms.MQMessageConsumer;
import com.ibm.mq.jms.MQMessageProducer;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSession;
import com.ibm.mq.jms.MQSession;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.v6.jms.internal.JMSC;


public class MQUtil {
	
	
	public static final String CORRELATIONID = "REQ\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"; 
	//上面的16进制写法
	public static final String JMS_CORRELATIONID = "ID:524551000000000000000000000000000000000000000000";
	
	public static final String JMS_UUID = "uuid";
	
	private static Logger logger = Logger.getLogger(MQUtil.class);
	
	/**
	 * 获取MQ队列连接
	 * @throws JMSException 
	 */
	public static MQQueueConnection getMQConnection(String ip, int port, String channel, String queueManager, int ccsid) throws JMSException{
		
		MQQueueConnection con = null;
		try{
			MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
			factory.setHostName(ip);
			factory.setPort(port);
			factory.setQueueManager(queueManager);
			factory.setChannel(channel);
			factory.setCCSID(ccsid);
			factory.setShareConvAllowed(0);
			//factory.setClientReconnectOptions(options)
			//此设置对应MQI的MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);
			factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
		//	factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
			con = (MQQueueConnection)factory.createQueueConnection();
		}catch(JMSException e){
			logger.error("MQ创建连接失败" + e.getMessage(), e);
			throw e;
		}
		
		return con;
	}
	

	/**
	 * 压缩集合，采用服务器——服务器方式和人行连接，不需要设置
	 * 在通道的发送通道和接收通道设置压缩格式
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection getMsgCompList(){
		ArrayList msgComp = new ArrayList();
		msgComp.add(new Integer( WMQConstants.WMQ_COMPMSG_ZLIBFAST));
		msgComp.add(new Integer(WMQConstants.WMQ_COMPMSG_RLE ));
		msgComp.add(new Integer(WMQConstants.WMQ_COMPMSG_ZLIBHIGH));
		msgComp.add(new Integer(WMQConstants.WMQ_COMPHDR_NONE));
		msgComp.add(new Integer(WMQConstants.WMQ_COMPMSG_DEFAULT));
		return msgComp;
	}
	
	/**
	 * 数据压缩分为消息头压缩和消息本身压缩
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection getHdrCompList(){
		
		ArrayList msgComp = new ArrayList();
		msgComp.add(new Integer( WMQConstants.WMQ_COMPHDR_DEFAULT));
		msgComp.add(new Integer( WMQConstants.WMQ_COMPHDR_SYSTEM));
		msgComp.add(new Integer( WMQConstants.WMQ_COMPHDR_NONE));
		return msgComp;
	}
	
	
	public static byte[] readByteFromMsg(Message message) throws JMSException{
		byte[] ret = null;
		if (message instanceof BytesMessage) {
			BytesMessage bm = (BytesMessage) message;
			ret = new byte[new Long(bm.getBodyLength()).intValue()];
			bm.readBytes(ret);
		} else if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage)message;
			if(tm.getText() != null){
				ret = tm.getText().getBytes();
			}
		}
		return ret;
	}
	
	public static void closeMQConnection(MQConnection o){
		
		if(o != null){
			try {
				//o.stop();
				o.close();
			} catch (JMSException e) {
				logger.error("关闭mq连接失败", e);
			}finally{
				o = null;
			}
		}
	}
	
	
	public static void closeMQSession(MQSession o){
		if(o != null){
			try {
				o.close();
			} catch (JMSException e) {
				logger.error("关闭mq会话失败", e);
			}finally{
				o = null;
			}
		}
	}
	
	public static void closeMQSession(ArrayList<MQQueueSession> sessions){
		
		if(sessions != null){
			for(MQQueueSession session : sessions){
				MQUtil.closeMQSession(session);
			}
		}
	}
	
	public static void colseMQMessageConsumer(ArrayList<MQQueueReceiver> consumers){
		
		if(consumers != null){
			for(MQQueueReceiver consumer : consumers){
				MQUtil.colseMQMessageConsumer(consumer);
			}
		}
	}
	
	public static void colseMQMessageProducer(MQMessageProducer o){
		if(o != null){
			try {
				o.close();
			} catch (JMSException e) {
				logger.error("关闭mq消息生产者失败", e);
			}finally{
				o = null;
			}
		}
	}
	
	public static void colseMQMessageConsumer(MQMessageConsumer o){
		if(o != null){
			try {
				o.close();
			} catch (JMSException e) {
				logger.error("关闭mq消息消费者失败", e);
			}finally{
				o = null;
			}
		}
	}
	
	public static String getJMSCorrelationID(String s){
		
		String receJMSCorrelationID = null;
		if(s.startsWith("ID:")){
			receJMSCorrelationID = s;
		}else{
			//receJMSCorrelationID ;
			String str = "ID:";
			for(int i=0; i<24; i++){
				int ch = '\0';
				if(i<s.length()){
					 ch = (int)s.charAt(i); 
				}
				String s4 = Integer.toHexString(ch);  
				if(s4.length() == 1){
					s4 = "0" + s4;
				}
				str = str+s4;
			}
			receJMSCorrelationID = str;
		}
		return receJMSCorrelationID;
	}
	
	public static String getJMSCorrelationIDSelector(String s){
		
		if(!s.contains("=")){
			return "JMSCorrelationID='" +  getJMSCorrelationID(s) + "'";
		}else{
			return s;
		}
	}
	
	public static void  main(String[] args){
		System.out.println(CORRELATIONID.getBytes());
		byte[] bs = CORRELATIONID.getBytes();
		for(int i=0;i<bs.length;i++){
			System.out.print(bs[i]);
		}
	}
}
