/**
 * 
 */
package com.jcodes.xml.xStream.sunline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对Java类中加了@XmlAttribute注解序列化转换成xml时，将会被转换成属性。
 * 反之亦然。
 * 
 * @author zx
 *
 */
@Target(ElementType.FIELD) //可以不使用
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlAttribute {

	//boolean required() default false;
}
