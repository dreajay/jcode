package com.jcodes.xml.xStream.sunline;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控件方法标记，有该标记的方法被认为是控件方法，后续IDE中使用
 * 
 * @author humy
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlMethod {

}
