package com.jcodes.hessian.client;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.caucho.hessian.client.HessianProxyFactory;
import com.jcodes.hessian.Car;
import com.jcodes.hessian.HelloHessian;

/**
 * 
 */
public class HessianClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HelloHessian hello = null;
		// 普通servlet方式调用
		hello = getHelloHessian();
		// spring集成发布服务
		hello = getHelloHessianBySpring();

		System.out.println(hello.sayHello());

		Car car = hello.getMyCar();
		System.out.println(car.toString());

		for (Map.Entry<String, String> entry : hello.myBabays().entrySet()) {
			System.out.println(entry.getKey() + "   " + entry.getValue());
		}

		for (String str : hello.myLoveFruit()) {
			System.out.println(str);
		}
	}

	public static HelloHessian getHelloHessian() throws Exception {
		// servlet发布的服务
		String url = "http://localhost:8080/jCodes-hessian/HessianService1";
		// spring发布的Hessian服务
		// url = "http://localhost:8080/jCodes-hessian/HessianService2";
		HessianProxyFactory factory = new HessianProxyFactory();
		HelloHessian hello = (HelloHessian) factory.create(HelloHessian.class, url);
		return hello;
	}

	public static HelloHessian getHelloHessianBySpring() {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-hessian-client.xml");
		return (HelloHessian) context.getBean("HessianService2");
	}

}
