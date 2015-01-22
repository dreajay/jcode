/**
 * 
 */
package com.jcodes.xml.xStream.sunline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对Java类中加了@XmlElement的字段注解序列化转换成xml时，将会被转换成子节点。
 * 若该字段是个Collection类型，则其泛型会是该字段的一堆子节点。
 * 反之亦然。
 * 
 * class window {
 * 		@XmlAttribute String left;
 * 		@XmlAttribute String name;
 * 		@XmlElemnt List<Item> items;
 * }
 * 
 * <window left="" name="">
 * 	 <items>
 *		<item></item>
 *		<item></item>
 *		<item></item>
 *		...
 *	 </items>
 * </window>
 * 
 * @author humy
 *
 */

@Target(ElementType.FIELD) //可以不使用
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElement {

}
