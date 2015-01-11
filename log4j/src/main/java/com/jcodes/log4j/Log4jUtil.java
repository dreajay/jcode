/**
 * 
 */
package com.jcodes.log4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.NOPLogger;
import org.apache.log4j.spi.NOPLoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;

public class Log4jUtil {
	

	/** Log4j开关 */
	public static boolean LOG4J_OPEN = true;
	/** 日志最大输出文本串长度 40K */
	public static final int MAX_LOGTEXT_LENGTG = 40960; 
	
	public static void initLog4jSimple() {
		BasicConfigurator.configure();
	}

	public static void initLog4jByXml(String logFilePath) throws IOException {
		if (logFilePath == null)
			throw new IOException("file not found: " + logFilePath);
		DOMConfigurator.configure(logFilePath);
	}

	public static void initLog4jByProperty(String logFilePath)
			throws IOException {
		if (logFilePath == null)
			throw new IOException("file not found: " + logFilePath);
//		PropertyConfigurator.configure(FileReadUtil.getProperties(logFilePath));
		PropertyConfigurator.configure(logFilePath);
	}
	
	private static NOPLogger nopLogger = new NOPLogger(new NOPLoggerRepository(), "NOPLogger");

	private static Map<Class<?>, Logger> loggers = new HashMap<Class<?>, Logger>();

	public static Logger getLogger(Object o) {
		/*if (Settings.MODE_DEV && !Logger.getRootLogger().getAllAppenders().hasMoreElements())
			initLog4jSimple();*/
		if (!LOG4J_OPEN) return nopLogger;
		
		Class<?> clazz = o.getClass();
		if (o instanceof Class<?>)
			clazz = (Class<?>) o;
		Logger logger = loggers.get(clazz);
		if (logger == null) {
			logger = Logger.getLogger(clazz);
			loggers.put(clazz, logger);
		}
		Throwable t = new Throwable();
		MDC.put("location", t.getStackTrace()[2]); // 出错处的堆栈信息
		return logger;
	}
	
	private static String getOutputMessage(Object message) {
		String ret = message.toString();
		if (ret.length() > MAX_LOGTEXT_LENGTG)
			return ret.substring(0, MAX_LOGTEXT_LENGTG) + "......";
		return ret;
	}

	public static void debug(Object o, Object message) {
		getLogger(o).debug(getOutputMessage(message));
	}

	public static void debug(Object o, Object message, Throwable t) {
		getLogger(o).debug(getOutputMessage(message), t);
	}

	public static void error(Object o, Object message) {
		getLogger(o).error(getOutputMessage(message));
	}

	public static void error(Object o, Object message, Throwable t) {
		getLogger(o).error(getOutputMessage(message), t);
	}

	public static void fatal(Object o, Object message) {
		getLogger(o).fatal(getOutputMessage(message));
	}

	public static void fatal(Object o, Object message, Throwable t) {
		getLogger(o).fatal(getOutputMessage(message), t);
	}

	public static void info(Object o, Object message) {
		getLogger(o).info(getOutputMessage(message));
	}

	public static void info(Object o, Object message, Throwable t) {
		getLogger(o).info(getOutputMessage(message), t);
	}

	public static void trace(Object o, Object message) {
		getLogger(o).trace(getOutputMessage(message));
	}

	public static void trace(Object o, Object message, Throwable t) {
		getLogger(o).trace(getOutputMessage(message), t);
	}
}
