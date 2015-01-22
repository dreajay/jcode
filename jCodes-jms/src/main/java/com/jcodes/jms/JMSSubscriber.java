package com.jcodes.jms;

import java.util.Hashtable;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

/**
 * 1. 在JNDI 名称空间查找TopicConnectionFactory 和Topic；
 * 2. 创建Topic Connection；                             
 * 3. 创建TopicSession；                                 
 * 4. 创建TopicSubscriber；                              
 * 5. 从Topic 接收预订；                                 
 * 6. 关闭并断开与连接对象的连接。                       
 * @author dreajay
 * Dec 14, 2012
 */
public class JMSSubscriber {

	public static void main(String[] args) {
		String topicName = "cn=psTopic";
		String tcfName = "cn=psTcf";
		Context jndiContext = null;
		TopicConnectionFactory topicConnectionFactory = null;
		TopicConnection topicConnection = null;
		TopicSession topicSession = null;
		Topic topic = null;
		TopicSubscriber subscriber = null;
		TextMessage message = null;
		String initialContextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
		String providerUrl = "file:/D:/JNDI";
//		String initialContextFactory = "com.ibm.ejs.ns.jndi.CNInitialContextFactory";
//		String providerUrl = "iiop://itsog:900/ptpCtx";
//		String providerUrl = "ldap://itsog/cn=psCtx,o=itsog,c=uk";
//		String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

		try {
			// Step 2 Set up Initial Context for JNDI lookup(步骤二，建立初始上下文以便JNDI 查找。)
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			env.put(Context.PROVIDER_URL, providerUrl);
			env.put(Context.REFERRAL, "throw");
			jndiContext = new InitialDirContext(env);
			
			// Step 3 Get the TopicConnection factory from the JNDI Namespace(步骤三，从JNDI 名称空间获取主题连接库。)
			topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup(tcfName);
			
			// Step 4 Create a TopicConnection(步骤四，创建主题连接。)
			topicConnection = topicConnectionFactory.createTopicConnection();
			
			// Step 5 Start The topic connection(步骤五，启动主题连接。)
			topicConnection.start();
			
			// Step 6 Create a topic session from the topic connection(步骤六，从主题连接创建主题会话。)
			topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			// Step 7 Obtain a Topic from the JNDI namespace(步骤七，从JNDI名称空间获取主题。)
			topic = (Topic) jndiContext.lookup(topicName);
			
			// Step 8 Create a topic subscriber for the topic.(步骤八，为主题创建主题订户。)
			subscriber = topicSession.createSubscriber(topic);// Non durable subscriber(非持久预定者)
			
			
			// Step 9 Receive Subscription(步骤九，接收预订。)
			message = (TextMessage) subscriber.receive();
			System.out.println("\n *** The Message is " + message.getText());
			
			// Step 10 Close the connection and other open resources(步骤十，关闭连接和其它打开资源。)
			subscriber.close();
			topicSession.close();
			topicConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
