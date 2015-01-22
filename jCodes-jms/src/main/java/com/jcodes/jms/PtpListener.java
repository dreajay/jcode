package com.jcodes.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class PtpListener implements MessageListener, ExceptionListener {

	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				System.out.println(((TextMessage) message).getText());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * onException. We will just print the exception and exit from the program execution. 
	 * Add suitable error handling and recovery logic depending on you use..
	 * （onException。我们将打印异常情况并且从程序的执行中退出。根据您的使用情况添加适当的错误 处理和恢复逻辑。）
	 */
	public void onException(JMSException e) {
		e.printStackTrace();
		System.exit(1);
	}
}