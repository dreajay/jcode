<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<title>登录</title>
</head>
<body onload="getErrMsg()">
	<form action="LoginServlet">
		<input type="text" name="username" placeholder="用户" /> <input
			type="password" name="password" placeholder="密码" /> <input
			type="submit" value="提交" />
	</form>
	<%
		Object errMsg = request.getAttribute("errMsg");
		System.err.println("errmsg:"+errMsg);
	%>
	<script type="text/javascript">
		function getErrMsg() {
			var error = "<%=errMsg%>";
			if (error != "" && error != "null" ) {
				alert(error);
			}
		}
	</script>
</body>
</html>