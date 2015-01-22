package com.jcodes.ognl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * Unit test for simple App.
 */
public class OgnlTest {

	public static void main(String[] args) {
		// 定义一个Map对象
		Map context = new HashMap();
		// 定义一个Team对象
		Team team1 = new Team();
		team1.setTeamname("团队1");
		// 定义一个Person对象
		Person person1 = new Person();
		person1.setName("pla1");
		// 添加team元素
		team1.setPerson(person1);
		// 定义一个Team对象
		Team team2 = new Team();
		team2.setTeamname("团队2");
		// 定义一个Person对象
		Person person2 = new Person();
		person2.setName("pla2");
		// 添加team元素
		team2.setPerson(person2);

		// 添加Map元素
		context.put("team1", team1);
		context.put("team2", team2);
		try {
			System.out.println(Ognl.getValue("team1.teamname", context));
			System.out.println(Ognl.getValue("team2.person.name", context));
			
			System.out.println(Ognl.getValue("teamname", team2));
			System.out.println(Ognl.getValue("person.name", team2));

			System.out.println(OgnlUtils.getValue("teamname", team1));
			OgnlUtils.setValue("teamname", team1, "jjj");
			System.out.println(OgnlUtils.getValue("teamname", team1));

			System.out.println(OgnlUtils
					.getValue("person.name", context, team1));
			System.out.println(OgnlUtils
					.getValue("person.name", context, team2));
		} catch (OgnlException e) {
			e.printStackTrace();
		}
	}
}
