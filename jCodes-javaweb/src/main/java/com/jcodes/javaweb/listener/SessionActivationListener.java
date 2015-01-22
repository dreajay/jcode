package com.jcodes.javaweb.listener;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * 实现HttpSessionActivationListener该接口的类(要求些javabean必须是实现了Serializable接口的)，能监测自己何时随着HttpSession一起激活和钝化。
 * Application Lifecycle Listener implementation class SessionL
 *
 */
public class SessionActivationListener implements HttpSessionActivationListener {

    /**
     * Default constructor. 
     */
    public SessionActivationListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionActivationListener#sessionDidActivate(HttpSessionEvent)
     */
    public void sessionDidActivate(HttpSessionEvent se)  {
    	System.out.println("活化了"); 
    }

	/**
     * @see HttpSessionActivationListener#sessionWillPassivate(HttpSessionEvent)
     */
    public void sessionWillPassivate(HttpSessionEvent se)  { 
    	System.out.println("钝化了");  
    }
	
}
