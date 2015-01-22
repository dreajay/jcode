package com.jcodes.javaweb.listener;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * 实现HttpSessionBindingListener该接口的类，能检测自己何时被Httpsession绑定，和解绑
 * Application Lifecycle Listener implementation class S
 *
 */
public class SessionBindingListener implements HttpSessionBindingListener {

    /**
     * Default constructor. 
     */
    public SessionBindingListener() {
    }

	/**
     * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent event)  { 
    	System.out.println("从从Session解除绑定");
    }

	/**
     * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent event)  { 
    	System.out.println("我被绑定到session");
    }
	
}
