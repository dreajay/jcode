<%@ page language="java" isErrorPage="true"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>错误页面</title>
</head>
<body bgcolor="#FFFFFF">
	<div align="center">
		<h1>错误信息</h1>
		<a href="javascript: history.back();">返回</a>
		<hr>
		<p>
		<h3><%=exception.toString()%></h3>
		<hr>

		<%
			String errorMsg = exception.getMessage();
			exception.printStackTrace(new PrintWriter(out));
		%>

	</div>
</body>
</html>