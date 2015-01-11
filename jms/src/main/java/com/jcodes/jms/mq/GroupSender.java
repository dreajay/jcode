package com.jcodes.jms.mq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class GroupSender {
	
//	private String qmgrName = "ITSOG.QMGR1";
//	private String queueName = "SAMPLE.QUEUE";
//	private String host = "ITSOG";
//	private String channel = "JAVA.CLIENT.CHNL";
	
	private String qmgrName = "FCBP";
	private String queueName = "OPENWIRE";
	private String host = "192.168.5.150";
	private String channel = "CLIENT.FCBP";
	
	private MQQueueManager qmgr;
	private MQQueue outQueue;
	private MQMessage outMsg;
	private MQPutMessageOptions pmo;

	private MQQueue inQueue;
	private MQMessage inMsg;
	private MQGetMessageOptions gmo;
	
	public static void main(String args[]) {
		GroupSender gs = new GroupSender();
		gs.runGoupSender();
	}

	public void runGoupSender() {
		try {
			init();
			sendGroupMessages();
			qmgr.commit();
			System.out.println("\n Messages successfully Send ");
		} catch (MQException mqe) {
			mqe.printStackTrace();
			try {
				System.out.println("\n Backing out Transaction ");
				qmgr.backout();
				System.exit(2);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	private void init() throws Exception {
		/** 设置MQEnvironment 属性以便客户机连接 */
		MQEnvironment.hostname = host;
		MQEnvironment.channel = channel;
		MQEnvironment.port = 1433;  
		// MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_WEBSPHERE
		// MQ);

		/* 连接到队列管理器 */
		qmgr = new MQQueueManager(qmgrName);

		/* 设置队列打开选项以便输出 */
		int opnOptn = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

		outQueue = qmgr.accessQueue(queueName, opnOptn, null, null, null);
	}

	private void sendGroupMessages() throws Exception {
		/* 设置放置消息选项 */
		pmo = new MQPutMessageOptions();
		pmo.options = pmo.options + MQC.MQPMO_LOGICAL_ORDER;
		pmo.options = pmo.options + MQC.MQPMRF_GROUP_ID;
		outMsg = new MQMessage();
		/* 设置消息标记，表示该消息属于组 */
		outMsg.messageFlags = MQC.MQMF_MSG_IN_GROUP;

		/* 把消息格式设置成串 */
		outMsg.format = MQC.MQFMT_STRING;
		String msgData = null;

		/* 把10 个简单消息作为一组发送 */
		int i = 10;
		while (i > 0) {
			msgData = "This is the " + i + "th message in the group ";
			outMsg.writeString(msgData);
			if (i == 1)
				outMsg.messageFlags = MQC.MQMF_LAST_MSG_IN_GROUP;
			i--;
			/* 每次放置一个消息到队列) */
			outQueue.put(outMsg, pmo);

			/* 清理缓冲区，以便重用 */
			outMsg.clearMessage();
		}
	}
	
	
	
}
