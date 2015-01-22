package com.jcodes.jms;

import java.util.Hashtable;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

public class PtpAsyncConsumer {
	String queueName = "cn=ptpQueue";
	String qcfName = "cn=ptpQcf";
	Context jndiContext = null;
	QueueConnectionFactory queueConnectionFactory = null;
	QueueConnection queueConnection = null;
	QueueSession queueSession = null;
	Queue queue = null;
	QueueReceiver queueReceiver = null;
	
	String initialContextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
	String providerUrl = "file:/D:/JNDI";
//	String initialContextFactory = "com.ibm.ejs.ns.jndi.CNInitialContextFactory";
//	String providerUrl = "iiop://itsog:900/ptpCtx";
//	String providerUrl = "ldap://itsog/cn=ptpCtx,o=itsog,c=uk";
//	String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

	public static void main(java.lang.String[] args) {
		try {
			PtpAsyncConsumer asyncConsumer = new PtpAsyncConsumer();
			asyncConsumer.performTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method performTask Control the flow of control of the logical operations
	 * (方法performTask 可控制逻辑操作的控制流)
	 */
	public synchronized void performTask() throws Exception {
		System.out.println("\n Setting Up Initial JNDI Context ");
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		env.put(Context.PROVIDER_URL, providerUrl);
		env.put(Context.REFERRAL, "throw");
		jndiContext = new InitialDirContext(env);
		System.out.println("\n Get QueueConnectionFactory ");
		queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup(qcfName);
		
		System.out.println("\n Get Queue ");
		queue = (Queue) jndiContext.lookup(queueName);
		
		System.out.println("\n Create Queue Connections ");
		queueConnection = queueConnectionFactory.createQueueConnection();
		
		//为连接对象queueConnection 设置了异常侦听器
		PtpListener jmsListener = new PtpListener();
		queueConnection.setExceptionListener(jmsListener);
		
		System.out.println("\n Start Queue Connection ");
		queueConnection.start();
		
		System.out.println("\n Create Queue Session ");
		queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		
		System.out.println("\n Create Queue Receiver ");
		queueReceiver = queueSession.createReceiver(queue);
		
		//注册消息侦听器
		System.out.println("\n Register the Listener");
		queueReceiver.setMessageListener(jmsListener);
		
		// Wait for new messages.(等待新消息。)
		wait();
	}
}