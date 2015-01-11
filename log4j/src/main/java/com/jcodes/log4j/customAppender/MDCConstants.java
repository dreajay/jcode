package com.jcodes.log4j.customAppender;

public interface MDCConstants {

	/** 默认的MDC组名称 */
	public final static String DEFALUT_GROUP_NAME = "sys";
	/** MDC组的key名称 */
	public final static String GROUP_KEY = "GROUP_KEY";
	/** MDC的执行id key名称，每个线程执行时的唯一ID序号 */
	public final static String EXECUTE_ID_KEY = "EXECUTE_ID_KEY";
}
