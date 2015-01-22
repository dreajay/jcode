/**
 * 
 */
package com.jcodes.xml.xStream.sunline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对Java类中加了@XmlElements注解的Java属性序列化转换成xml时，不会被转换成子节点
 * 而是将其List或者其他Collection类型的泛型转换成子节点。
 * 反序列化亦然。
 * 
 * class window {
 * 		@XmlAttribute String left;
 * 		@XmlAttribute String name;
 * 		@XmlElemnts List<Item> items;
 * }
 * 
 * <window left="" name="">
 *		<item></item>
 *		<item></item>
 *		<item></item>
 *		...
 * </window>
 * 
 * @author humy
 *
 */

@Target(ElementType.FIELD) //可以不使用
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElements {

}

