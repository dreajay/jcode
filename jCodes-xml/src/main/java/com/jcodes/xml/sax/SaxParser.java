package com.jcodes.xml.sax;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jcodes.xml.Constants;
import com.jcodes.xml.Parser;
import com.jcodes.xml.Toolkit;

/**
 * SAX解析器
 * 
 * @author zangweiren 2010-4-17
 * 
 */
public class SaxParser extends DefaultHandler implements Parser {

	@Override
	public String getName() {
		return "SaxParser";
	}

	// 解析
	public void parse(String xmlFile) {
		SAXParserFactory f = SAXParserFactory.newInstance();
		try {
			SAXParser sp = f.newSAXParser();
			sp.parse(new File(xmlFile), new SaxParser());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseAndTime(String xmlFile) {
		Toolkit.startTime(this);
		parse(xmlFile);
		Toolkit.endTime(this);
	}

	@Override
	public void parseBigFile() {
		parseAndTime(Constants.BIG_XML_FILE);
	}

	@Override
	public void parseMiddleFile() {
		parseAndTime(Constants.MIDDLE_XML_FILE);
	}

	@Override
	public void parseSmallFile() {
		parseAndTime(Constants.SMALL_XML_FILE);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) {
		if ("user".equalsIgnoreCase(qName)) {
			System.out.println("Name:" + attrs.getValue("name") + ";age="
					+ attrs.getValue("age") + ";gender="
					+ attrs.getValue("gender"));
		}
	}
}
