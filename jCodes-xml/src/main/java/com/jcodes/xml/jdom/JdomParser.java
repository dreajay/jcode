package com.jcodes.xml.jdom;

import java.io.File;
import java.io.IOException;
import java.util.List;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.jcodes.xml.Constants;
import com.jcodes.xml.Parser;
import com.jcodes.xml.Toolkit;

/**
 * JDOM解析器
 * 
 * @author zangweiren 2010-4-17
 * 
 */
public class JdomParser implements Parser {

	@Override
	public String getName() {
		return "JdomParser";
	}

	private void parse(String xmlFile) {
		SAXBuilder sax = new SAXBuilder();
		try {
			Document doc = sax.build(new File(xmlFile));
			printUsers(doc);
		} catch (JDOMException e) {
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

	@SuppressWarnings("unchecked")
	private void printUsers(Document doc) {
		Element root = doc.getRootElement();
		List children = root.getChildren();
		for (int i = 0; i < children.size(); i++) {
			Element depart = (Element) children.get(i);
			List users = depart.getChildren();
			for (int j = 0; j < users.size(); j++) {
				Element user = (Element) users.get(j);
				System.out.println("Name:" + user.getAttributeValue("name")
						+ ";age:" + user.getAttributeValue("age") + ";gender:"
						+ user.getAttributeValue("gender") + ";title:"
						+ user.getTextTrim());
			}
		}
	}
}
