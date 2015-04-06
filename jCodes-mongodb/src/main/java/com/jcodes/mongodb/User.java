package com.jcodes.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {

	private String id;
	private String name;
	private int age;
	private List<String> interest;
	private String wife;
	private Home home;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName1(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<String> getInterest() {
		return interest;
	}

	public void setInterest(List<String> interest) {
		this.interest = interest;
	}

	public String getWife() {
		return wife;
	}

	public void setWife(String wife) {
		this.wife = wife;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}
}