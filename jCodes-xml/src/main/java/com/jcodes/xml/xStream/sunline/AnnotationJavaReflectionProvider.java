package com.jcodes.xml.xStream.sunline;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

/**
 * XStream注解反射提供器，对与带有Property注解的属性才允许处理
 * 
 * 使用获取java的默认构造器进行反射构造类对象
 * 由于调用构造器构造对象，故对于xml中不存在但java类中存在的属性设置成java属性的默认值
 * 
 * @author humy
 *
 */
public class AnnotationJavaReflectionProvider extends PureJavaReflectionProvider {

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
        super.validateFieldAccess(field);
    }

    /**
     * 写字段时验证字段可以被访问，且对于String类型需要进行trim
     */
    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
        validateFieldAccess(field);
        try {
        	if (value instanceof String)
        		field.set(object, ((String)value).trim());
        	else field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        }
    }
}


