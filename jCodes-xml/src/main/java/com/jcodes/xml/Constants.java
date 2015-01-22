package com.jcodes.xml;

/**
 * 常量
 * 
 * @author zangweiren 2010-4-17
 * 
 */
public interface Constants {
	public static final String FILE_DIR = Constants.class.getResource("/")
			.getFile();

	// XML文件的位置
	public static final String SMALL_XML_FILE = FILE_DIR + "/smallusers.xml";// 100K
	public static final String MIDDLE_XML_FILE = FILE_DIR + "/middleusers.xml";// 1M
	public static final String BIG_XML_FILE = FILE_DIR + "/bigusers.xml";// 10M
}
