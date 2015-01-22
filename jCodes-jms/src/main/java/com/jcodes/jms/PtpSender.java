package com.jcodes.jms;

import java.util.Hashtable;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

/**
 * 在JNDI 名称空间查找QueueConnectionFactory 和Queue；
 * 获取Queue Connection 对象；
 * 创建QueueSession；
 * 创建QueueSender；
 * 创建TextMessage；
 * 发送消息到Queue；
 * 关闭以及断开与连接对象的连接。
 * jmsadmin
 * 定义名为FCBP_QCF队列连接工厂：
 * def qcf(FCBP_QCF) hostname(192.168.5.138) port(1433) qmanager(FCBP) channel(CLIENT.FCBP) transport(CLIENT)
 * 定义名为OPENWIRE的队列
 * def q(OPENWIRE) queue(OPENWIRE) qmanager(FCBP)：定义名为OPENWIRE 的队列
 * @author dreajay
 * Dec 14, 2012
 */
public class PtpSender {
	public static void main(String[] args) {
		String queueName = "OPENWIRE";
		String qcfName = "FCBP_QCF";
		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueSender queueSender = null;
		TextMessage message = null;
		String initialContextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
		String providerUrl = "file:/C:/JNDI-Directory";
		
//		String initialContextFactory = "com.ibm.ejs.ns.jndi.CNInitialContextFactory";
//		String providerUrl = "iiop://itsog:900/ptpCtx";
		
		/**
		 * Step 2 set up an Initial Context for JNDI lookup(步骤二，建立初始上下文以便JNDI
		 * 查找)
		 */
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			env.put(Context.PROVIDER_URL, providerUrl);
			
			// env.put(Context.REFERRAL, "throw") ;
			jndiContext = new InitialDirContext(env);
			
			/**
			 * Step 3 get a QueueConnectionFactory. We will retrieve the
			 * QueueConnectionFacotry object named ptpQcf created in Persistent
			 * Name Server using JMSAdmin
			 * tool.(步骤三，获取QueueConnectionFactory。我们将接收QueueConnectionFactory 对象
			 * 指定的使用JMSAdmin 工具在Persistent Name Server 里创建的ptpQcf。)
			 */
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup(qcfName);
			/**
			 * Step 4 the Queue object from the JNDI namespace.(步骤四，来自JNDI名称空间的队列对象)
			 */
			queue = (Queue) jndiContext.lookup(queueName);
			
			/**
			 * Step 5 Create a QueueConnection from the QueueConnectionFactory(从队列连接库创建队列连接)
			 */
			queueConnection = queueConnectionFactory.createQueueConnection();
			
			/**
			 * Step 6 Start the QueueConnection.(步骤六，启动队列连接)
			 */
			queueConnection.start();
			
			/**
			 * Step 7 Create a QueueSession object from the QueueConnection.(步骤七，从队列连接创建队列会话)
			 */
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			/**
			 * Step 8 Create a QueueSender object for sending messages from the queue session.
			 * (步骤八，创建队列发送器 以便从队列会话发送消息)
			 */
			queueSender = queueSession.createSender(queue);
			
			/**
			 * Step 9 prepare a message object from the queuesession. we will create a textMessage message object.
			 * (步骤九，从队列会话准备消息对象。我们将创建textMessage 消息对象。)
			 */
			message = queueSession.createTextMessage();
			
			/**
			 * Step 10 Set the message you want, to the message object.(步骤十，设置您想要的消息到消息对象。)
			 */
			message.setText("This is a Test Message from PtpSender Class ");
			
			/**
			 * Step 11 Now we are ready to send the message.(步骤十一，现在我们已准备好发送消息。)
			 */
			queueSender.send(message);
			
			System.out.println("\n The Message has been sent");
			/**
			 * Step 12 Close the Queue Connection Before exiting from the program.(步骤十二，在从程序退出前关闭队列 连接)
			 */
			queueConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}