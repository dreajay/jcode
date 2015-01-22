package com.jcodes.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

@WebService(endpointInterface = "com.jcodes.webservice.IMyService")
public class MyServiceImpl implements IMyService {
	List<User> list = new ArrayList<User>();

	@Override
	public int add(int a, int b) {
		System.out.println(a + "+" + b + "=" + (a + b));
		return a + b;
	}

	@Override
	public User login(String username, String password) {
		System.out.println(username + " is logining");
		User user = new User();
		user.setId(1);
		user.setUsername(username);
		user.setPassword(password);
		return user;
	}

	@Override
	public User addUser(int id, String username, String password) {
		User user = new User(id, username, password);
		list.add(user);
		return user;
	}

	@Override
	public List<User> list() {
		return list;
	}

}
