package com.jcodes.xml.xStream.sunline;


import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

/**
 * 一个实现xml2java和java2xml对象转换的工具类<br/>
 * 默认使用xpp3 xml解析器,更快速度解析<br/>
 * 
 * FIXME 由于使用了field.getAnnotation(XmlElements.class) 
 * 如果是出于对速度的要求，建议使用util = new XStreamUtil(mode);，并将util对象复用以加快访问速度
 * 
 * @author zx
 *
 */
public class XStreamUtil {
	
	public static final Integer MODE_JAVA = 1;
	public static final Integer MODE_SUN = 2;
	
	private XStream xstream = null;
	
	/**
	 * @param mode 
	 * 	MODE_JAVA - 使用java构造函数方式构造对象，如果对象中的字段设置了初始值，则初始值存在
	 * 	MODE_SUN  - 使用sun代码构造对象，由于没有调用构造器，所以如果对象中的字段设置了初始值，
	 * 			则不起作用，字段值还是null，如果基本类型则为基本类型默认值（非类字段的默认值）
	 */
	public XStreamUtil(Integer mode) {
		if (mode == MODE_JAVA)
			xstream = new XStream(new AnnotationJavaReflectionProvider());
		else if (mode == MODE_SUN)
			xstream = new XStream(new AnnotationSunReflectionProvider());
		else xstream = new XStream();
	}
	
	XStreamUtil(ReflectionProvider rp) {
		xstream = new XStream(rp);
	}
	
	/**
	 * 设置类加载器，用于解决不alias和调用处的ClassLoader不相同的问题
	 */
	public void setClassLoader(ClassLoader classLoader) {
		xstream.setClassLoader(classLoader);
	}
	
	public void setAlias(Map<String, Class<?>> alias) {
		setAliasAndAttribute(alias, alias.values(), xstream);
	}
	
	public String toXML(Object o) {
		return xstream.toXML(o);
	}
	
	public void toXML(Object o, OutputStream out) {
		xstream.toXML(o, out);
	}
	
	public Object fromXML(InputStream is) {
		return xstream.fromXML(is);
	}
	
	public Object fromXML(String str) {
		return xstream.fromXML(str);
	}

	/**
	 * 将java对象转化成xml字符串 AnnotationSunReflectionProvider映射器
	 * 
	 * @param o		java对象
	 * @param alias	含类型与别名对应关系的一个map类，用于设置别名与对象解析
	 * @return
	 */
	public static String object2xmlSun(Object o, Map<String, Class<?>> alias) {
		XStreamUtil util = new XStreamUtil(MODE_SUN);
		util.setAlias(alias);
		return util.toXML(o);
	}
	
	/**
	 * 将java对象转化成xml字符串  AnnotationJavaReflectionProvider映射器
	 * 
	 * @param o		java对象
	 * @param alias	含类型与别名对应关系的一个map类，用于设置别名与对象解析
	 * @return
	 */
	public static String object2xmlJava(Object o, Map<String, Class<?>> alias) {
		XStreamUtil util = new XStreamUtil(MODE_JAVA);
		util.setAlias(alias);
		return util.toXML(o);
	}
	
	/**
	 * 将输入流is对应的xml字符串转化成java对象  AnnotationSunReflectionProvider映射器
	 */
	public static Object xml2ObjectSun(InputStream is, Map<String, Class<?>> alias){
		XStreamUtil util = new XStreamUtil(MODE_SUN);
		util.setAlias(alias);
		return util.fromXML(is);
	}
	
	public static Object xml2ObjectSunWithClassLoader(InputStream is, Map<String, Class<?>> alias, ClassLoader classLoader){
		XStreamUtil util = new XStreamUtil(MODE_SUN);
		util.setClassLoader(classLoader);
		util.setAlias(alias);
		return util.fromXML(is);
	}
	
	/**
	 * 将输入流is对应的xml字符串转化成java对象  AnnotationJavaReflectionProvider映射器
	 * 
	 * @param is
	 * @param alias
	 * @return
	 */
	public static Object xml2ObjectJava(InputStream is, Map<String, Class<?>> alias){
		XStreamUtil util = new XStreamUtil(MODE_JAVA);
		util.setAlias(alias);
		return util.fromXML(is);
	}
	
	public static Object xml2ObjectJavaWithClassLoader(InputStream is, Map<String, Class<?>> alias, ClassLoader classLoader){
		XStreamUtil util = new XStreamUtil(MODE_JAVA);
		util.setClassLoader(classLoader);
		util.setAlias(alias);
		return util.fromXML(is);
	}
	
	/**
	 * 将xml字符串转化成java对象
	 * 
	 * @param rp	反射提供器，AnnotationSunReflectionProvider或AnnotationJavaReflectionProvider
	 * @param xmlstr
	 * @param alias
	 * @return
	 */
	static Object xml2Object (ReflectionProvider rp, String xmlStr, Map<String, Class<?>> alias){
		XStreamUtil util = new XStreamUtil(rp);
		util.setAlias(alias);
		return util.fromXML(xmlStr);
	}

	/**
	 * 为xstream设置alias、attr以及子节点
	 * @param alias
	 * @param clazzs
	 * @param xstream
	 */
	private void setAliasAndAttribute(Map<String, Class<?>> alias,
			Collection<Class<?>> clazzs, XStream xstream) {
		for (Map.Entry<String, Class<?>> en : alias.entrySet())
			xstream.alias(en.getKey(), en.getValue());
		
		// 带@XmlAttribute注解的字段为属性，其他则为子节点
		for (Class<?> clazz : clazzs) {
			for (String attrName : getXmlAttributes(clazz))
				xstream.useAttributeFor(clazz, attrName);
		}
		
		// 带@XmlElements注解的字段为列表，且去除当前这一级节点
		for (Class<?> clazz : clazzs) {
			for (Object[] collectionName : getXmlElements(clazz)) {
				xstream.addImplicitCollection(clazz, (String)collectionName[0], (Class<?>)collectionName[1]);
			}
		}
	}
	
	/**
	 * 取得clazz类型能够序列化成xml属性的类属性列表
	 * @param clazz
	 * @return
	 */
	private static List<String> getXmlAttributes(Class<?> clazz) {
		List<String> ret = new ArrayList<String>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(XmlAttribute.class) != null) {
				ret.add(field.getName());
			}
		}
		return ret;
	}
	
	/**
	 * 取得clazz类型能够序列化成xml子元素集的类属性列表
	 * @param clazz
	 * @return
	 */
	private List<Object[]> getXmlElements(Class<?> clazz) {
		List<Object[]> ret = new ArrayList<Object[]>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(XmlElements.class) != null) {
				  if (field.getGenericType() instanceof ParameterizedType) {
						ParameterizedType p = (ParameterizedType)field.getGenericType();
						if (p.getActualTypeArguments().length > 0) {
							Type fanxType = p.getActualTypeArguments()[0];
							if (fanxType instanceof Class<?>) {
								Class<?> fan = (Class<?>)fanxType;
								Object [] fields = new Object [2];
								fields[1] = fan;
								fields[0] = field.getName();
								ret.add(fields);
							}
						}
					}
			}
		}
		return ret;
	}
}
