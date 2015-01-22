package com.jcodes.javaweb.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jcodes.javaweb.SessionConstant;

public class AuthorityFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String requestURI = req.getRequestURI();

		// 如果请求不为登录页面,则进行检查用session内容,如果为登录页面就不去检查.
		if (!requestURI.endsWith("login.jsp") && !requestURI.contains("LoginServlet") && !requestURI.contains("error")) {
			// 取得session. 如果没有session则自动会创建一个,
			// 我们用false表示没有取得到session则设置为session为空.
			HttpSession session = req.getSession(false);
			// 如果session中没有任何东西.
			if (session == null || session.getAttribute(SessionConstant.USER) == null) {
				res.sendRedirect(req.getContextPath() + "/login.jsp");
				// 返回
				return;
			}
		}
		// session中的内容等于登录页面, 则可以继续访问其他区资源.
		chain.doFilter(req, res);
	}

	
}