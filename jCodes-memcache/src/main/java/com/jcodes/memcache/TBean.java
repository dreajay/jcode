package com.jcodes.memcache;

import java.io.Serializable;

public class TBean implements Serializable {

	private static final long serialVersionUID = 1945562032261336919L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}