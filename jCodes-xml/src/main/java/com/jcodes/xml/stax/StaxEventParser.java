package com.jcodes.xml.stax;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.jcodes.xml.Constants;
import com.jcodes.xml.Parser;
import com.jcodes.xml.Toolkit;

/**
 * StAX迭代式解析器
 * 
 * @author zangweiren 2010-4-18
 * 
 */
public class StaxEventParser implements Parser {
	private String getAttributeValue(StartElement e, String qname) {
		return e.getAttributeByName(new QName(qname)).getValue();
	}

	@Override
	public String getName() {
		return "StaxEventParser";
	}

	public void parse(String xmlFile) {
		XMLInputFactory f = XMLInputFactory.newInstance();
		try {
			XMLEventReader r = f.createXMLEventReader(new FileReader(xmlFile));
			printUsers(r);
			r.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
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

	private void printUsers(XMLEventReader reader) throws XMLStreamException {
		XMLEvent event = reader.nextEvent();
		while (true) {
			if (event.isStartElement()) {
				StartElement e = event.asStartElement();
				if ("user".equalsIgnoreCase(e.getName().toString())) {
					System.out.println("Name:" + getAttributeValue(e, "name")
							+ ";age=" + getAttributeValue(e, "age")
							+ ";gender=" + getAttributeValue(e, "gender"));
				}
			} else if (event.isEndDocument()) {
				break;
			}
			event = reader.nextEvent();
		}
	}
}
