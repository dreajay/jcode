package com.jcodes.xml.dom;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jcodes.xml.Constants;
import com.jcodes.xml.Parser;
import com.jcodes.xml.Toolkit;

/**
 * DOM(JAXP Crimson解析器)
 * 
 * @author zangweiren 2010-4-17
 * 
 */
public class DomParser implements Parser {

	@Override
	public String getName() {
		return "DomParser";
	}

	private void parse(String xmlFile) {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder b = f.newDocumentBuilder();
			Document doc = b.parse(new File(xmlFile));
			printUsers(doc);
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

	private void printUsers(Document doc) {
		NodeList nodes = doc.getElementsByTagName("user");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			NamedNodeMap attributes = node.getAttributes();
			System.out.println("Name:"
					+ attributes.getNamedItem("name").getNodeValue() + ";age="
					+ attributes.getNamedItem("age").getNodeValue()
					+ ";gender="
					+ attributes.getNamedItem("gender").getNodeValue());
		}
	}
}
