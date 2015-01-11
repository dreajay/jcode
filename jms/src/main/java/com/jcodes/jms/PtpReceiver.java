package com.jcodes.jms;

import java.util.Hashtable;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

/**
 * 在JNDI 名称空间中查找QueueConnectionFactory 和Queue；
 * 获取Queue Connection 对象；
 * 创建QueueSession；
 * 创建QueueReceiver；
 * 接收消息并显示它；
 * 关闭以及断开与连接对象的连接。
 * @author dreajay
 * Dec 14, 2012
 */
public class PtpReceiver {
	
	public static void main(String[] args) {
		String queueName = "OPENWIRE";
		String qcfName = "FCBP_QCF";
		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueReceiver queueReceiver = null;
		TextMessage message = null;
		/*
		 * Provider url /For Persistent Name Server－
		 * iiop://iiopservername/contextname /For LDAP Server use
		 * ldap//cn=ContextName,o=OrganizationalSuffix,c=coutrysuffix eg.
		 * ldap://machineName/cn=ptpCtx,o=itso,c=uk
		 */
		String initialContextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
		String providerUrl = "file:/C:/JNDI-Directory";
		
//		String initialContextFactory = "com.ibm.ejs.ns.jndi.CNInitialContextFactory";
//		String providerUrl = "iiop://itsog:900/ptpCtx";
		
		
		/**
		 * Step 2 set up Initial Context for JNDI lookup(步骤二，建立初始上下文以便JNDI 查找)
		 */
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			env.put(Context.PROVIDER_URL, providerUrl);
			// env.put(Context.REFERRAL, "throw") ;
			jndiContext = new InitialDirContext(env);
			/**
			 * Step 3 get the QueueConnectionFactory from the JNDI
			 * Namespace(步骤三，从JNDI 名称空间获取队列连 接库)
			 */
			queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup(qcfName);
			
			/**
			 * Step 4 get the Queue Object from the JNDI Name space(步骤四，从JNDI名称空间获取队列对象)
			 */
			queue = (Queue) jndiContext.lookup(queueName);
			
			/**
			 * Step 5 Create a QueueConnection using the QueueConnectionFactory(步骤五，利用队列连接库创建队列连 接)
			 */
			queueConnection = queueConnectionFactory.createQueueConnection();
			
			/*
			 * Step 6 Connections are always created in stopped mode. You have to Explicitly start them. 
			 * Start the queueConnection(步骤六，在停止模式里创建连接。您必须明确的启动他们。启动队 列连接。)
			 */
			queueConnection.start();
			
			/**
			 * Step 7 Create a queueSession object from the QueueConnection(步骤七，从队列连接创建队列会话对象)
			 */
			queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			
			/**
			 * Step 8 Create a QueueReceiver from the queueSession(步骤八，从队列会话创建队列接收器)
			 */
			queueReceiver = queueSession.createReceiver(queue);
			
			/**
			 * Step 9 Receive Messages. Here we are implementing a synchronous message receiver. 
			 * The receive call is in a loop so that it would process all the available messages in the queue
			 * (步骤九，接收消息。在此我们实施同步消息接收。循环进行接收调用从而 能在队列中处理所有可用消息。)
			 */
			boolean eom = true;
			while (eom) {
				Message m = queueReceiver.receive(1);
				if (m != null) {
					if (m instanceof TextMessage) {
						message = (TextMessage) m;
						System.out.println("Reading message: " + message.getText());
					} else {
						break;
					}
				} else
					eom = false;
			}
			
			/**
			 * Step 10 Close the connections(步骤十，关闭连接)
			 */
			queueConnection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
