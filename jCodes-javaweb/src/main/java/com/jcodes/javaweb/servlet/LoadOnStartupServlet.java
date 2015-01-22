package com.jcodes.javaweb.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jcodes.javaweb.SessionConstant;

/**
 * web启动时加载初始化工作，设置load-on-startup参数
 * Servlet implementation class LoginServlet
 */
public class LoadOnStartupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("LoadOnStartupServlet init...");
	}
	
    @Override
	public void destroy() {
		super.destroy();
		System.out.println("LoadOnStartupServlet destroy...");
	}

	

	/**
     * @see HttpServlet#HttpServlet()
     */
    public LoadOnStartupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("接收请求");
	}

}
