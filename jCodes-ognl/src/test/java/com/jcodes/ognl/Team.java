package com.jcodes.ognl;

import java.util.Date;

public 
//团队类
class Team {
	// 团队名称
	private String teamname;
	// 定义团队人员属性
	private Person person;
	// 团队人数
	private int personnum;

	// 属性的getter和setter方法
	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public int getPersonnum() {
		return personnum;
	}

	public void setPersonnum(int personnum) {
		this.personnum = personnum;
	}
}

//定义人员类
class Person {
	// 姓名
	private String name;
	// 年龄
	private int age;
	// 人员出生日期
	private Date birthday;

	// 属性的getter和setter方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
}
