package com.jcodes.jms.spring;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

/**
 * 
 * @author dreajay
 *
 */
public class Consumer {

	private JmsTemplate jmsTemplate;

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * 监听到消息目的有消息后自动调用onMessage(Message message)方法
	 */
	public void recive() throws JMSException {
		TextMessage message = (TextMessage) jmsTemplate.receive();
		String msg = message.getText();
		System.out.println("接收信息:\n" + msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws JMSException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "classpath:applicationContext.xml" });
		
		Consumer consumer = (Consumer) applicationContext
				.getBean("consumer");
		consumer.recive();
		

	}
	
}