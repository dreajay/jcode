package com.jcodes.hessian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caucho.hessian.server.HessianServlet;

public class HelloHessianImpl extends HessianServlet implements HelloHessian {

	private static final long serialVersionUID = -8514138792429263077L;

	public Car getMyCar() {
		Car car = new Car();
		car.setCarName("阿斯顿·马丁");
		car.setCarModel("One-77");
		return car;
	}

	public Map<String, String> myBabays() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("son", "孙吴空");
		map.put("daughter", "孙小美");
		return map;
	}

	public List<String> myLoveFruit() {
		List<String> list = new ArrayList<String>();
		list.add("apple");
		list.add("kiwi");
		list.add("orange");
		return list;
	}

	public String sayHello() {
		return "welcom to Hessian";
	}

}