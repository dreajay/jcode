package com.jcodes.javaweb.listener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 利用HttpSessionListener初略统计在线人数
 * Application Lifecycle Listener implementation class OnlineCounterListener
 *
 */
public class OnlineCounterListener implements HttpSessionListener {

    /**
     * Default constructor. 
     */
    public OnlineCounterListener() {
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se)  { 
    	ServletContext servletContext = se.getSession().getServletContext();  
        Integer count = (Integer) servletContext.getAttribute("count");  
        if(count == null)  
            count = 1;  
        else  
            count ++;  
        servletContext.setAttribute("count", count);  
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se)  { 
    	 ServletContext servletContext = se.getSession().getServletContext();  
         Integer count = (Integer) servletContext.getAttribute("count");  
         servletContext.setAttribute("count", --count);  
    }
	
    
}
