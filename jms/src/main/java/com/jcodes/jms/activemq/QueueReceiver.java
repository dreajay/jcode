package com.jcodes.jms.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 同步接收消息
 * @author dreajay
 *
 */
public class QueueReceiver {
	public static void main(String[] args) throws JMSException {
		// 通过username,password,url创建连接工厂接口
		ConnectionFactory factory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
		// 通过连接工厂创建一个新的连接接口
		Connection connection = factory.createConnection();
		connection.start();
		// 通过连接接口创建一个会话接口
		final Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		// 会话接口创建有关主题的目标接口
		Destination destination = session.createQueue("queue_test");
//		Destination destination = session.createQueue("queue_test_replay");
		// 会话接口再根据目标接口来创建一个消息消费者接口
		MessageConsumer consumer = session.createConsumer(destination);
		//指定消息选择器，只接收指定选择器的消息，表达式符合SQL语法，发送消息的时候需要设置选择器值
//		MessageConsumer consumer = session.createConsumer(destination, "JMSCorrelationID ='123456789'");
		
		//同步接收消息：只能接收一次消息
		// 1.阻塞等待消息，直到收到信息才返回
//		TextMessage message = (TextMessage) consumer.receive();
		// 2.阻塞等待消息，超出指定超时时间或收到信息就不等待
//		TextMessage message = (TextMessage) consumer.receive(3000);
//		 3. 不阻塞等待消息，有消息直接返回，无消息返回null
//		TextMessage message = (TextMessage) consumer.receiveNoWait();
//		if (null != message) {
//			System.out.println("收到消息：" + message.getText());
//		} else {
//			System.out.println("没有收到消息！");
//		}
		
		//异步消息，使用监听器来接收消息，当有消息的时候，会自动调用onMessage方法，可以多次接收消息，确保Session不要关闭
		consumer.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				// 消息消费者接收消息
				if (null != message) {
					try {
						String msg = ((TextMessage) message).getText();
						System.out.println("收到消息：" + msg);
						//根据JMSReplyTo获取返回Destination
						Destination replyDdestination = message.getJMSReplyTo();
						//创建一个新的MessageProducer来发送一个回复消息。  
			            MessageProducer producer = session.createProducer(replyDdestination);  
			            producer.send(session.createTextMessage("Hello " + msg)); 
					} catch (JMSException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("没有收到消息！");
				}
				
			}
		});
		
		// 关闭会话，设置监听消息的时候不要关闭
//		session.close();
		// 关闭连接，设置监听消息的时候不要关闭
//		connection.close();
	}
}
