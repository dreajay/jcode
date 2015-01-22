package com.jcodes.xml.stax;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.jcodes.xml.Constants;
import com.jcodes.xml.Parser;
import com.jcodes.xml.Toolkit;

/**
 * StAX指针式解析器
 * 
 * @author zangweiren 2010-4-18
 * 
 */
public class StaxStreamParser implements Parser {
	@Override
	public String getName() {
		return "StaxStreamParser";
	}

	public void parse(String xmlFile) {
		XMLInputFactory f = XMLInputFactory.newInstance();
		try {
			XMLStreamReader r = f
					.createXMLStreamReader(new FileReader(xmlFile));
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

	private void printUsers(XMLStreamReader reader) throws XMLStreamException {
		int event = reader.next();
		while (true) {
			if (XMLStreamConstants.START_ELEMENT == event) {
				if ("user".equalsIgnoreCase(reader.getName().toString())) {
					System.out.println("Name:"
							+ reader.getAttributeValue(null, "name") + ";age="
							+ reader.getAttributeValue(null, "age")
							+ ";gender="
							+ reader.getAttributeValue(null, "gender"));
				}
			} else if (XMLStreamConstants.END_DOCUMENT == event) {
				break;
			}
			event = reader.next();
		}
	}
}
