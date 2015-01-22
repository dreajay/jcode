package com.jcodes.jms.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueSender {
	public static void main(String[] args) throws JMSException {
		// 通过username,password,url创建连接工厂接口
		ConnectionFactory factory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
		// 通过连接工厂创建一个新的连接接口
		Connection connection = factory.createConnection();
		// 通过连接接口创建一个会话接口
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		// 会话接口创建有关主题的目标接口
		Destination destination = session.createQueue("queue_test");
		// 会话接口再根据目标接口来创建一个消息生产者接口
		MessageProducer producer = session.createProducer(destination);
		//设置消息生存时间，超过该时间，消息会被移除，不设置永久
//		producer.setTimeToLive(60000);
		//设置消息优先级0~9个级别，0最低，9最高
		producer.setPriority(5);
		
		//设置消息持久性:DeliveryMode.PERSISTENT,DeliveryMode.NON_PERSISTENT，默认为PERSISTENT
		//持久性消息只被传输一次，非持久性消息最多被传输一次，有可能是0次，比如，JMS提供者故障时消息被丢失的时候
		//可以分别测试设置PERSISTENT和NON_PERSISTENT，当关闭JMS重新启动
//		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		// 调用会话生成一个文本消息
		Message message = session.createTextMessage("Hello World!");
		
		//设置消息选择器，当有多个应用都需要从同一个队列取消息的时候，可以通过指定消息选择器，来进行消息过滤
		//消息头中的可以使用选择器的属性：JMSDeliveryMode、JMSPriority、JMSMessageID、JMSTimestamp、JMSCorrelationID、JMSType
		//这里使用JMSCorrelationID来指定消息选择器，消息的接收者在创建的时候需要指定
		//MessageConsumer consumer = session.createConsumer(dest, "JMSMessageID ='123456789'");
		
		//使用producer.setDeliveryMode
//		message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
		
		//设置消息优先级0~9个级别，0最低，9最高，如果设置了producer.setPriority，则优先级以producer设置的为准
		message.setJMSPriority(4);
		
		//设置消息时间戳
		message.setJMSTimestamp(System.currentTimeMillis());
		
		//设置消息过期时间，如果设置为0标示永不过期，使用上面的方法：producer.setTimeToLive
//		message.setJMSExpiration(5);
		
		//设置消息类型
		message.setJMSType("TEST");
		
		//设置消息惟一标识，只能在消息发送后才能确定
		//message.setJMSMessageID("123456789");
		
		//设置消息返回时发送给指定目标Destination
		message.setJMSReplyTo(session.createQueue("queue_test_replay"));
		
		
		// 通过生产者接口Send将消息发布到ActiveMQ服务器
		producer.send(message);
		//消息发送后获取的JMSMessageID和自己指定的不一样，这是自动生成的
		System.out.println(message.getJMSMessageID());
		
		// 关闭会话
		session.close();
		// 关闭连接
		connection.close();
	}
}
