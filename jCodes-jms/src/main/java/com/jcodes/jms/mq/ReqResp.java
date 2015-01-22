package com.jcodes.jms.mq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class ReqResp {

	// public static String hostName = "ITSOG";
	// public static String channel = "JAVA.CLIENT.CHNL";
	// public static String qManager = "ITSOG.QMGR1";
	// public static String requestQueue = "SAMPLE.REQUEST";
	// public static String replyToQueue = "SAMPLE.REPLY";
	// public static String replyToQueueManager = "ITSOG.QMGR1";
	// public static String qName = "SAMPLE.REQUEST";

	public static String hostName = "192.168.5.150";
	public static int port = 1433;
	public static int CCSID = 1381;
	public static String channel = "CLIENT.FCBP";
	public static String qManager = "FCBP";
	public static String qName = "OPENWIRE";
	public static String requestQueue = "OPENWIRE";
	public static String replyToQueue = "OPENWIRE";
	public static String replyToQueueManager = "FCBP";

	public static void requester() {
		try {

			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.channel = channel;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = CCSID;

			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qManager);

			/* 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也已设置了选项去应对不 成功情况 */
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开打开队列 */
			MQQueue queue = qMgr.accessQueue(requestQueue, openOptions, null, null, null);

			/* 设置放置消息选项，我们将使用默认设置 */
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = pmo.options + MQC.MQPMO_NEW_MSG_ID;
			pmo.options = pmo.options + MQC.MQPMO_SYNCPOINT;

			/* 创建消息缓冲区 */
			MQMessage outMsg = new MQMessage();
			/* 设置MQMD 格式字段 */
			outMsg.format = MQC.MQFMT_STRING;
			outMsg.messageFlags = MQC.MQMT_REQUEST;
			outMsg.replyToQueueName = replyToQueue;
			outMsg.replyToQueueManagerName = replyToQueueManager;
			
			/* 准备用户数据消息 */
			String msgString = "Test Request Message from Requester program ";
			outMsg.writeString(msgString);
			/* 在队列上放置消息 */
			queue.put(outMsg, pmo);
			/* 提交事务 */
			qMgr.commit();
			System.out.println(" The message has been Successfully put\n");
			/* 关闭请求队列 */
			queue.close();
			/* 设置打开选项以便队列响应 */
			openOptions = MQC.MQOO_INPUT_SHARED | MQC.MQOO_FAIL_IF_QUIESCING;
			MQQueue respQueue = qMgr.accessQueue(replyToQueue, openOptions, null, null, null);
			MQMessage respMessage = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			/* 在同步点控制下获取消息 */
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;
			gmo.options = gmo.options + MQC.MQGMO_WAIT;
			gmo.matchOptions = MQC.MQMO_MATCH_CORREL_ID;
			gmo.waitInterval = 300000000;
			System.out.println("outMsg.messageId:"+new String(outMsg.messageId));
			respMessage.correlationId = outMsg.messageId;
			respMessage.correlationId = "12345".getBytes();
			/* 获取响应消息 */
			respQueue.get(respMessage, gmo);
			String response = respMessage.readString(respMessage.getMessageLength());
			System.out.println("The response message is : " + response);
			qMgr.commit();
			respQueue.close();
			qMgr.disconnect();
		} catch (MQException ex) {
			System.out.println("An MQ Error Occurred: Completion Code is :\t" + ex.completionCode + "\n\n The Reason Code is :\t" + ex.reasonCode);
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理来自请求队列的请求消息并发送回复到请求应用程序指定的请求队列上。
	 */
	public static void responder() {
		try {

			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.channel = channel;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = CCSID;
			// MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_WEBSPHERE
			// MQ);
			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qManager);
			/*
			 * 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也 已设置了选项去应对不成功情况
			 */
			int openOptions = MQC.MQOO_INPUT_SHARED | MQC.MQOO_FAIL_IF_QUIESCING;
			/* 打开队列 */
			MQQueue queue = qMgr.accessQueue(qName, openOptions, null, null, null);
			/* 设置放置消息选项 */
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			/* 在同步点控制下取消息 */
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;
			/* 如果队列上没有消息则等待 */
			gmo.options = gmo.options + MQC.MQGMO_WAIT;
			/* 如果队列管理器停止则失败 */
			gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;
			/* 设置等待的时间限制 */
			gmo.waitInterval = 3000000;
			/* 创建MQMessage 类 */
			MQMessage inMsg = new MQMessage();
			/* 从队列到队列缓冲区获取消息 */
			queue.get(inMsg, gmo);
			/* 从消息读用户数据 */
			String msgString = inMsg.readString(inMsg.getMessageLength());
			System.out.println(" The Message from the Queue is : " + msgString);
			/* 检查看消息是否属于类型请求消息并对该请求回复 */
			if (inMsg.messageFlags == MQC.MQMT_REQUEST) {
				System.out.println("Preparing To Reply To the Request ");
				String replyQueueName = inMsg.replyToQueueName;
				openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
				MQQueue respQueue = qMgr.accessQueue(replyQueueName, openOptions, inMsg.replyToQueueManagerName, null, null);
				MQMessage respMessage = new MQMessage();
				
				MQPutMessageOptions pmo = new MQPutMessageOptions();
				respMessage.format = MQC.MQFMT_STRING;
				respMessage.messageFlags = MQC.MQMT_REPLY;
				
//				respMessage.correlationId = "123456789".getBytes();
//				String response = "Reply from the Responder Program ";
//				respMessage.writeString(response);
//				respQueue.put(respMessage, pmo);
				inMsg.messageId = "12345".getBytes();
				respMessage.correlationId = inMsg.messageId;
				String response = "Reply from the Responder Program ";
				respMessage.writeString(response);
				respQueue.put(respMessage, pmo);
				
				
				
				System.out.println("The response Successfully send ");
				qMgr.commit();
				respQueue.close();
			}
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
		new Thread(new Runnable() {
			public void run() {
				requester();
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				responder();
			}
		}).start();

		
	}
}
