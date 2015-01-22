package com.jcodes.xml.dom4j;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jcodes.xml.Constants;
import com.jcodes.xml.Parser;
import com.jcodes.xml.Toolkit;

/**
 * DOM4J解析器
 * 
 * @author zangweiren 2010-4-17
 * 
 */
public class Dom4jParser implements Parser {

	@Override
	public String getName() {
		return "Dom4jParser";
	}

	private void parse(String xmlFile) {
		SAXReader r = new SAXReader();
		try {
			Document doc = r.read(new File(xmlFile));
			printUsers(doc);
		} catch (DocumentException e) {
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
		Iterator departs = root.elementIterator("depart");
		while (departs.hasNext()) {
			Element depart = (Element) departs.next();
			Iterator users = depart.elementIterator("user");
			while (users.hasNext()) {
				Element user = (Element) users.next();
				System.out.println("Name:" + user.attributeValue("name")
						+ ";age=" + user.attributeValue("age") + ";gender="
						+ user.attributeValue("gender"));
			}
		}
	}
}
