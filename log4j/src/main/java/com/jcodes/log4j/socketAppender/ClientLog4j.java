package com.jcodes.log4j.socketAppender;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.xml.DOMConfigurator;

import com.jcodes.log4j.customAppender.GroupRollingFileAppender;

public class ClientLog4j {

	static class Runner extends Thread {
		public static int i = 1;
		Logger logger = Logger.getLogger(ClientLog4j.class);

		// Logger logger = LogManager.getLogger("testRemote");
		public void run() {
			this.setName("Thread_" + i);
			final String s = "Group_" + i;

			i++;

			MDC.put(GroupRollingFileAppender.GROUP_KEY, s);

			for (int k = 0; k < 10; k++) {
				logger.info(this.getName() + " " + System.currentTimeMillis(),
						new Exception("123"));
				logger.info(s + " " + System.currentTimeMillis() + ":JJJJJJJJJ"
						+ " " + k);
			}

		}

	}

	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure(ClientLog4j.class.getResource(".").getPath()
				+ "log4j_client.xml");
		for (int i = 0; i < 1; i++)
			new Runner().start();

		// test SocketAppender, 从 log4j.xml获取Logger
		Logger remoteLogger = LogManager.getLogger("remote");
		remoteLogger.info("blabla");
	}
}
