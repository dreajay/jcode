package com.jcodes.jms.spring;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * 
 * @author dreajay
 *
 */
public class Producer {
	private Destination destination;
	private JmsTemplate jmsTemplate;

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public Destination getDestination() {
		return this.destination;
	}
	
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	
	public void sendMessage(final String msg) {
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				System.out.println("发送了一条JMS消息："+msg);
				return session.createTextMessage(msg);
			}
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "classpath:applicationContext.xml" });
		
		Producer topicSender = (Producer) applicationContext
				.getBean("topicSender");
		String msg = "Hello ActiveMQ Text Message！";
		topicSender.sendMessage(msg);
		for (int i = 0; i < 10; i++) {
			topicSender.sendMessage(i+"-"+msg);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}