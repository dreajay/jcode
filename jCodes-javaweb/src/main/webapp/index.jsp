<%@ page language="java" errorPage="error.jsp"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>

<%-- <%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
 --%>
<!DOCTYPE html>
<html>
<head>
<%-- 静态引入,引入内容和当前页面融合到一起再编译成servlet，先包含，后编译 --%>
<%@ include file="head.jsp"%>
<title>首页</title>
</head>
<body>
	<h2>Hello World!</h2>
	<%--这个是注释 --%>
	
	<%-- 测试异常信息，会转到错误页面 --%>
	<%
		//int b= Integer.parseInt("测试异常");
	%>
	<%--表达式--%>
	<%=5%>


	<%-- 动态引入，先编译，后包含 --%>
	<jsp:include page="include_dynamic.jsp" flush="true"></jsp:include>


</body>
</html>
