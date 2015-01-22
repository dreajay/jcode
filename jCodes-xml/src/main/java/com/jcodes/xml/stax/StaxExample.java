package com.jcodes.xml.stax;


import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StaxExample {

	@Test
	public void test01() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		//命名空间开关
//		factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			XMLStreamReader reader = factory.createXMLStreamReader(is);
			while(reader.hasNext()) {
				int type = reader.next();
				//判断节点类型是否是开始或者结束或者文本节点,之后根据情况及进行处理
				if(type==XMLStreamConstants.START_ELEMENT) {
					System.out.println(reader.getName());
				} else if(type==XMLStreamConstants.CHARACTERS) {
					System.out.println(reader.getText().trim());
				} else if(type==XMLStreamConstants.END_ELEMENT) {
					System.out.println("/"+reader.getName());
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 输出book属性
	 */
	@Test
	public void test02() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			XMLStreamReader reader = factory.createXMLStreamReader(is);
			while(reader.hasNext()) {
				int type = reader.next();
				if(type==XMLStreamConstants.START_ELEMENT) {
					String name = reader.getName().toString();
					if(name.equals("book")) {
						System.out.println(reader.getAttributeName(0)+":"+reader.getAttributeValue(0));
					}
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test03() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			XMLStreamReader reader = factory.createXMLStreamReader(is);
			while(reader.hasNext()) {
				int type = reader.next();
				
				if(type==XMLStreamConstants.START_ELEMENT) {
					String name = reader.getName().toString();
					if(name.equals("title")) {
						System.out.print(reader.getElementText()+":");
					}
					if(name.equals("price")) {
						System.out.print(reader.getElementText()+"\n");
					}
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test04() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			//基于迭代模型的操作方式
			XMLEventReader reader = factory.createXMLEventReader(is);
			int num = 0;
			while(reader.hasNext()) {
				//通过XMLEvent来获取是否是某种节点类型
				XMLEvent event = reader.nextEvent();
				if(event.isStartElement()) {
					//通过event.asxxx转换节点
					String name = event.asStartElement().getName().toString();
					if(name.equals("title")) {
						System.out.print(reader.getElementText()+":");
					}
					if(name.equals("price")) {
						System.out.print(reader.getElementText()+"\n");
					}
				}
				num++;
			}
			System.out.println(num);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test05() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			//基于Filter的过滤方式，可以有效的过滤掉不用进行操作的节点，效率会高一些
			XMLEventReader reader = factory.createFilteredReader(factory.createXMLEventReader(is),
					new EventFilter() {
						@Override
						public boolean accept(XMLEvent event) {
							//返回true表示会显示，返回false表示不显示
							if(event.isStartElement()) {
								String name = event.asStartElement().getName().toString();
								if(name.equals("title")||name.equals("price"))
									return true;
							}
							return false;
						}
					});
			int num = 0;
			while(reader.hasNext()) {
				//通过XMLEvent来获取是否是某种节点类型
				XMLEvent event = reader.nextEvent();
				if(event.isStartElement()) {
					//通过event.asxxx转换节点
					String name = event.asStartElement().getName().toString();
					if(name.equals("title")) {
						System.out.print(reader.getElementText()+":");
					}
					if(name.equals("price")) {
						System.out.print(reader.getElementText()+"\n");
					}
				}
				num++;
			}
			System.out.println(num);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test06() {
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			//创建文档处理对象
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			//通过DocumentBuilder创建doc的文档对象
			Document doc = db.parse(is);
			//创建XPath
			XPath xpath = XPathFactory.newInstance().newXPath();
			//第一个参数就是xpath,第二参数就是文档
			NodeList list = (NodeList)xpath.evaluate("//book[@category='WEB']", doc,XPathConstants.NODESET);
			for(int i=0;i<list.getLength();i++) {
				//遍历输出相应的结果
				Element e = (Element)list.item(i);
				System.out.println(e.getElementsByTagName("title").item(0).getTextContent());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void test07() {
		try {
			XMLStreamWriter xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(System.out);
			xsw.writeStartDocument("UTF-8","1.0");
			xsw.writeEndDocument();
			String ns = "http://11:dd";
			xsw.writeStartElement("nsadfsadf","person",ns);
			xsw.writeStartElement(ns,"id");
			xsw.writeCharacters("1");
			xsw.writeEndElement();
			xsw.writeEndElement();
			xsw.flush();
			xsw.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test08() {
		InputStream is = null;
		try {
			is = StaxExample.class.getClassLoader().getResourceAsStream("books.xml");
			//创建文档处理对象
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			//通过DocumentBuilder创建doc的文档对象
			Document doc = db.parse(is);
			//创建XPath
			XPath xpath = XPathFactory.newInstance().newXPath();
			Transformer tran = TransformerFactory.newInstance().newTransformer();
			tran.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			tran.setOutputProperty(OutputKeys.INDENT, "yes");
			//第一个参数就是xpath,第二参数就是文档
			NodeList list = (NodeList)xpath.evaluate("//book[title='Learning XML']", doc,XPathConstants.NODESET);
			//获取price节点
			Element be = (Element)list.item(0);
			Element e = (Element)(be.getElementsByTagName("price").item(0));
			e.setTextContent("333.9");
			Result result = new StreamResult(System.out);
			//通过tranformer修改节点
			tran.transform(new DOMSource(doc), result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
