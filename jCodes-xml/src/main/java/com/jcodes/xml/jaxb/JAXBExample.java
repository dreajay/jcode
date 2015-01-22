package com.jcodes.xml.jaxb;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * JAXB是Java Architecture for XML Binding的缩写，可以将Java对象和XML相互转换。
 * @author dreajay
 */
public class JAXBExample {
	public static void main(String[] args) {
		object2xml();
		xml2object();
	}

	public static void object2xml() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			// 编排：从Java对象编排成XML
			Marshaller marshaller = jaxbContext.createMarshaller();
			// 格式化输出
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// 设置编码
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			Customer customer = new Customer();
			customer.setId(1);
			customer.setName("张三");
			customer.setAge(100);
			// 输出
			marshaller.marshal(customer, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public static void xml2object() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><customer id=\"1\"><age>100</age><name>张三</name></customer>";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			// 反编排：从XML反编码为Java对象
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Customer customer = (Customer) unmarshaller
					.unmarshal(new StringReader(xml));
			System.out.println(customer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
