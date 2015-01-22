<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	System.out.println("jsp include page");
%>

<hr />

<h3>这是动态引入界面的内容</h3>

<%
	Thread.sleep(1000 * 3);

	System.out.println("jsp include page complete....");
%>