package com.jcodes.jms.mq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * 
 * 
 * @author dreajay Dec 7, 2012
 */
public class Ptp {

	public static String hostName = "192.168.5.150";
	public static int port = 1433;
	public static int CCSID = 1381;
	public static String channel = "CLIENT.FCBP";
	public static String qManager = "FCBP";
	public static String qName = "OPENWIRE";

	/**
	 * 点到点客户机程序将创建一个简单的消息并发送它到WebSphere MQ 队列
	 * 
	 * 步骤如下： 调入WebSphere MQ Java API package; 为客户机连接设置环境属性; 连接到队列管理器;
	 * 为打开WebSphere MQ 队列设置选项; 为发送消息打开应用程序队列; 设置选项， 放置消息到应用程序队列上; 创建消息缓冲区;
	 * 使用用户数据和任何消息描述器字段准备消息; 放置消息到队列上。
	 * 
	 * @param args
	 */
	public static void ptpSender() {
		try {

			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.channel = channel;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = CCSID;
			// MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,
			// MQC.TRANSPORT_MQJD);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qManager);

			/* 设置打开选项以便打开用于输出的队列，如果队列管理器正在停止，我们也已设置了选项去应对不成功情况。 */
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			MQQueue queue = qMgr.accessQueue(qName, openOptions, null, null, null);

			/* 设置放置消息选项，我们将使用默认设置 */
			MQPutMessageOptions pmo = new MQPutMessageOptions();

			/* 创建消息缓冲区，MQMessage 类包含实际消息数据的数据缓冲区，和描述消息的所有MQMD 参数 */
			MQMessage outMsg = new MQMessage();
			/* 设置MQMD 格式字段 */
			outMsg.format = MQC.MQFMT_STRING;
			/* 准备用户数据消息 */
			String msgString = "Test Message from PtpSender program ";
			outMsg.writeString(msgString);

			/* 在队列上放置消息 */
			queue.put(outMsg, pmo);

			/* 提交事务处理 */
			qMgr.commit();
			System.out.println(" The message has been Successfully put！\n");

			/* 关闭队列和队列管理器对象 */
			queue.close();
			qMgr.disconnect();
		} catch (MQException ex) {
			System.out.println("An MQ Error Occurred: Completion Code is :\t" + ex.completionCode + "\n\n The Reason Code is :\t" + ex.reasonCode);
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点到点客户机程序是消息接收器应用程序，它获取PtpSender 应用程序所发送的消息并在控制台上将消息打印出来。
	 * 
	 * 步骤如下： 调入WebSphere MQ Java API package; 为客户机连接设置环境属性; 连接到队列管理器;
	 * 为打开WebSphere MQ 队列设置选项; 为获取消息打开应用程序; 设置选项， 从应用程序队列获取消息; 创建消息缓冲区;
	 * 从队列获取消息到消息缓冲区; 从消息缓冲区读取用户数据并在控制台上显示。
	 * 
	 * @param args
	 */
	public static void ptpReceiver() {

		try {

			/** 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.channel = channel;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = CCSID;
			
			// MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_PROPERTY);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qManager);

			/* 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也已设置了选项去应对不成功情况 */
			int openOptions = MQC.MQOO_INPUT_SHARED | MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			MQQueue queue = qMgr.accessQueue(qName, openOptions, null, null, null);

			/* 设置放置消息选项 */
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			/* 在同步点控制下获取消息 */
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;
			/* 如果在队列上没有消息则等待 */
			gmo.options = gmo.options + MQC.MQGMO_WAIT;
			/* 如果队列管理器停顿则失败 */
			gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;
			/* 设置等待的时间限制 */
			gmo.waitInterval = 3000;

			/* 创建MQMessage 类 */
			MQMessage inMsg = new MQMessage();
			/* 从队列到消息缓冲区获取消息 */
			queue.get(inMsg, gmo);
			/* 从消息读取用户数据 */
			String msgString = inMsg.readString(inMsg.getMessageLength());
			System.out.println(" The Message from the Queue is : " + msgString);

			/* 提交事务 */
			qMgr.commit();

			/* 关闭队列和队列管理器对象 */
			queue.close();
			qMgr.disconnect();

		} catch (MQException ex) {
			System.out.println("An MQ Error Occurred: Completion Code is :\t" + ex.completionCode + "\n\n The Reason Code is :\t" + ex.reasonCode);
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ptpSender();
		ptpReceiver();
	}
}