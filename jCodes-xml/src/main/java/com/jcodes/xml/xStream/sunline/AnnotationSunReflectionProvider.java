package com.jcodes.xml.xStream.sunline;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;

/**
 * XStream注解反射提供器，对与带有Property注解的属性才允许处理
 * 
 * 使用sun的ReflectionFactory进行反射构造类对象(不需要任何构造器就可以构建)
 * 由于未调用默认构造器，故对于xml中不存在但java类中存在的属性设置成null
 * 
 * @author zhangx
 *
 */
@SuppressWarnings("unchecked")
public class AnnotationSunReflectionProvider extends Sun14ReflectionProvider {

	private Object getValidFieldByAnnotation(Field field) {
		Object p = field.getAnnotation(XmlAttribute.class);
    	if (p == null) p = field.getAnnotation(XmlElement.class);
    	if (p == null) p = field.getAnnotation(XmlElements.class);
		return p;
	}

    protected boolean fieldModifiersSupported(Field field) {
    	Object p = getValidFieldByAnnotation(field);
        return p != null && (!(Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())));
    }

    protected void validateFieldAccess(Field field) {
    	Object p = getValidFieldByAnnotation(field);
    	if (p == null)
            throw new ObjectAccessException("Invalid final field "
                    + field.getDeclaringClass().getName() + "." + field.getName());
        if (Modifier.isFinal(field.getModifiers())) {
            if (JVM.is15()) {
                field.setAccessible(true);
            } else {
                throw new ObjectAccessException("Invalid final field "
                        + field.getDeclaringClass().getName() + "." + field.getName());
            }
        }
    }

    /**
     * 写字段时验证字段可以被访问，且对于String类型需要进行trim
     */
    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
    	Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
    	validateFieldAccess(field);
    	if (value instanceof String)
    		super.writeField(object, fieldName, ((String)value).trim(), definedIn);
    	else 
    		super.writeField(object, fieldName, value, definedIn);
    }
}


