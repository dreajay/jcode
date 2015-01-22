package com.jcodes.webservice.client;


import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class WSClient {
	public static void main(String[] args) {
		try {
			//创建访问wsdl服务地址的url
			URL url = new URL("http://localhost:8888/ns?wsdl");
			//通过Qname指明服务的具体信息
			QName sname = new QName("http://webservice.jcodes.com/", "MyServiceImplService");
			//创建服务
			Service service = Service.create(url,sname);
			//实现接口
			IMyService ms = service.getPort(IMyService.class);
			System.out.println(ms.add(12,33));
			//以上服务有问题，依然依赖于IMyServie接口
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		//直接根据生成的MyServiceImplService获得IMyService接口进行调用
		MyServiceImplService myServiceImplService = new MyServiceImplService();
		IMyService myService = myServiceImplService.getMyServiceImplPort();
		System.out.println(myService.add(10, 20));
	}
}
