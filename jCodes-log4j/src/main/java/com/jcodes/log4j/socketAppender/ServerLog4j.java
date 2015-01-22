package com.jcodes.log4j.socketAppender;

import org.apache.log4j.net.SocketServer;


public class ServerLog4j {


	public static void main(String[] args) throws Exception {
		args = new String[3];
		//服务器的端口号
		args[0] = "4560";
		//log4j 配置文件
		args[1] = ServerLog4j.class.getResource(".").getPath()+"log4j_server.properties";
		//generic.lcf文件所在路径
		args[2] = ServerLog4j.class.getResource(".").getPath();
		SocketServer.main(args);

	}
}
