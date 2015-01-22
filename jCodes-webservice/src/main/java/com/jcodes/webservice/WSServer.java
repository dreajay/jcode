package com.jcodes.webservice;

import javax.xml.ws.Endpoint;

public class WSServer {

	public static void main(String[] args) {
		String address = "http://localhost:8888/ns";
		// 发布webservice服务，Java可使用wsimport工具转化服务为Java类
		Endpoint.publish(address, new MyServiceImpl());
		
		
	}

}
