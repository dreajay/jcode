package com.jcodes.jms;

import java.util.Hashtable;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

/**
 * 1. 定义JMS 管理对象
 * 2. 在JNDI 名称空间查找TopicConnectionFactory 和Topic；
 * 3. 创建Topic Connection；
 * 4. 创建TopicSession；
 * 5. 创建TopicPublisher；
 * 6. 创建TextMessage；
 * 7. 发布消息至Topic；
 * 8. 关闭以及断开与连接对象的连接。
 * @author dreajay
 * Dec 14, 2012
 */
public class JMSPublisher {

	public static void main(String[] args) {
		String topicName = "cn=psTopic";
		String tcfName = "cn=psTcf";
		Context jndiContext = null;
		TopicConnectionFactory topicConnectionFactory = null;
		TopicConnection topicConnection = null;
		TopicSession topicSession = null;
		Topic topic = null;
		TopicPublisher publisher = null;
		TextMessage message = null;
		String initialContextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
		String providerUrl = "file:/D:/JNDI";
//		String initialContextFactory = "com.ibm.ejs.ns.jndi.CNInitialContextFactory";
//		String providerUrl = "iiop://itsog:900/ptpCtx";
//		String providerUrl = "ldap://itsog/cn=psCtx,o=itsog,c=uk";
//		String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

		// Step 2 Set up an Initial context for JNDI lookUp.(步骤二，建立初始上下文以便JNDI 查找。)
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			env.put(Context.PROVIDER_URL, providerUrl);
			jndiContext = new InitialDirContext(env);

			// Step 3 Obtain a TopicConnection factory(步骤三，获取主题连接库。)
			topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup(tcfName);

			// Step 4 Create a Topic Connection using the connection factory object(步骤四，使用连接库对象创建主题连接。)
			topicConnection = topicConnectionFactory.createTopicConnection();
			
			// Step 5 Start the topic connection.(步骤五，启动主题连接。)
			topicConnection.start();
			
			// Step 6 Obtain a Topic from the JNDI(步骤六，从JNDI 获取主题。)
			topic = (Topic) jndiContext.lookup(topicName);
			
			// Step 7 Create a Topic Session from the topic connection(步骤七，从主题连接创建主题会话。)
			topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			
			// Step 8 Create a topic publisher for the topic from the session.(步骤八，从会话为主题创建主题发布者。)
			publisher = topicSession.createPublisher(topic);
			
			// Step 9 Create a message object(步骤九，创建消息对象。)
			message = topicSession.createTextMessage();

			// Step 10 prepare the body of the message(步骤十，准备消息主体。)
			message.setText("This is a Test Message from JMSPublisher Class ");
			
			// Step 11 Publish the message.(步骤十一，发布消息。)
			publisher.publish(message);
			
			// Step 12 Close the connections.(步骤十二，关闭连接。)
			publisher.close();
			
			topicSession.close();
			topicConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}