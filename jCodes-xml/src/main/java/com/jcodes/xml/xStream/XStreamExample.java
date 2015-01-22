package com.jcodes.xml.xStream;

import com.thoughtworks.xstream.XStream;

public class XStreamExample {

	public static void main(String[] args) {

		XStream xstream = new XStream();
		// XStream xstream = new XStream(new DomDriver()); // does not require
		// XPP3 library
		// XStream xstream = new XStream(new StaxDriver()); // does not require
		// XPP3 library starting with Java 6

		xstream.alias("person", Person.class);
		xstream.alias("phonenumber", PhoneNumber.class);

		Person joe = new Person("Joe", "Walnes");
		joe.setPhone(new PhoneNumber(123, "1234-456"));
		joe.setFax(new PhoneNumber(123, "9999-999"));

		// convert to xml
		String xml = xstream.toXML(joe);

		System.out.println(xml);

		// convert to object
		Person newJoe = (Person) xstream.fromXML(xml);

		System.out.println(newJoe);

	}

}
