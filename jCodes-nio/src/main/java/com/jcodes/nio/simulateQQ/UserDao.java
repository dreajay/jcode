package com.jcodes.nio.simulateQQ;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

	public static List<Users> getUsers() {
		List<Users> users = new ArrayList<Users>();
		Users user1 = new Users("tiantian", "123456");
		Users user2 = new Users("dongdong", "123456");
		Users user3 = new Users("xiaoxiao", "123456");
		Users user4 = new Users("mingming", "123456");
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		return users;
	}
}