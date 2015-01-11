package com.jcodes.jms.mq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * 有关步骤如下：
 调入必需的包；
 为客户机连接设置MQEnvironment 属性；
 连接到队列管理器；
 设置队列打开选项以获得输入；
 打开队列以获得输入；
 设置放置消息选项；
	- 设置选项以便在同步点控制下获得消息；
	- 设置选项，当在组里提供所有消息时，以便只处理消息；
	- 设置选项以便以逻辑顺序处理信息；
 创建消息缓冲区；
 设置消息标题属性；
 创建个体消息并将其放置到队列上；
 从队列上获取消息直到处理完最后消息；
 在控制台上显示消息内容；
 提交事务处理。
 * @author dreajay
 * Dec 10, 2012
 */
public class GroupReceiver {

//	private String qmgrName = "ITSOG.QMGR1";
//	private String queueName = "SAMPLE.QUEUE";
//	private String host = "ITSOG";
//	private String channel = "JAVA.CLIENT.CHNL";
	
	private String qmgrName = "FCBP";
	private String queueName = "OPENWIRE";
	private String host = "192.168.5.150";
	private String channel = "CLIENT.FCBP";
	
	private MQQueueManager qmgr;
	private MQQueue inQueue;
	private MQMessage inMsg;
	private MQGetMessageOptions gmo;

	public static void main(String args[]) {
		GroupReceiver gs = new GroupReceiver();
		gs.runGoupReceiver();
	}

	public void runGoupReceiver() {
		try {
			init();
			getGroupMessages();
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
		// Set the MQEnvironment Properties for client
		// connection(为客户机连接设置MQEnvironment 属性)
		MQEnvironment.hostname = host;
		MQEnvironment.channel = channel;
		MQEnvironment.port = 1433;  
		MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);
		// Connect to the Queue Manager(连接到队列管理器)
		qmgr = new MQQueueManager(qmgrName);
		// Set the Queue open option for input(设置队列打开选项以输入)
		int opnOptn = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_FAIL_IF_QUIESCING;
		// Open the Queue for input(打开队列以输入)
		inQueue = qmgr.accessQueue(queueName, opnOptn, null, null, null);
	}

	private void getGroupMessages() throws Exception {
		// Set the get message options(设置获取消息选项)
		gmo = new MQGetMessageOptions();
		gmo.options = MQC.MQGMO_FAIL_IF_QUIESCING;
		gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;
		gmo.options = gmo.options + MQC.MQGMO_WAIT;
		// Wait for messages(等待消息)
		gmo.waitInterval = 5000;
		// Set wait time limit in ms(在ms 里设置等待时间限制)
		gmo.options = gmo.options + MQC.MQGMO_ALL_MSGS_AVAILABLE; // Getmessages
		// only when all(只获取消息) messages of the group are available(当提供所有组消息时)
		gmo.options = gmo.options + MQC.MQGMO_LOGICAL_ORDER;
		// Get messages in the logical order.(以逻辑顺序获取消息)
		gmo.matchOptions = MQC.MQMO_MATCH_GROUP_ID;
		// Create the message buffer.(创建消息缓冲区)
		inMsg = new MQMessage();
		String msgData = null;
		// Process the messages of the group(处理组消息)
		while (true) {
			inQueue.get(inMsg, gmo);
			int msgLength = inMsg.getMessageLength();
			msgData = inMsg.readString(msgLength);
			System.out.println("The message is \n " + msgData);
			char x = gmo.groupStatus;
			// Check for the last message flag(检查看是否是最后消息标记)
			if (x == MQC.MQGS_LAST_MSG_IN_GROUP) {
				System.out.println("B Last Msg in Group");
				break;
			}
			inMsg.clearMessage();
		}
	}
}