package com.jcodes.xml;

/**
 * XML解析器接口
 * 
 * @author zangweiren 2010-4-18
 * 
 */
public interface Parser {

	public String getName();

	public void parseBigFile();

	public void parseMiddleFile();

	public void parseSmallFile();
}