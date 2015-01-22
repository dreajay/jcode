package com.jcodes.ognl;

import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * 使用ognl表达式从上下文或对象中获取值<br>
 * <p>
 * OGNL 表达式 <br>
 * 1.常量： 字符串：“ hello ” 字符：‘ h ’ 数字：除了像 java 的内置类型 int,long,float 和 double,Ognl
 * 还有如例：10.01B，相当于 java.math.BigDecimal，使用’ b ’或者’ B ’后缀。 100000H，相当于
 * java.math.BigInteger，使用’ h ’ 或 ’ H ’ 后缀。 <br>
 * 2.属性的引用 例如：user.name <br>
 * 3.变量的引用 例如：#name <br>
 * 4.静态变量的访问 使用 @class@field <br>
 * 5.静态方法的调用 使用 @class@method(args), 如果没有指定 class 那么默认就使用 java.lang.Math. <br>
 * 6.构造函数的调用 例如：new java.util.ArrayList();<br>
 * 7.Array:{1,2,3}，Map:#{"k1":"v1","k2":"v2"}
 * </p>
 * <p>
 * 
 * 参考：<br>
 * http://commons.apache.org/proper/commons-ognl/developer-guide.html
 * http://www.ibm.com/developerworks/cn/opensource/os-cn-ognl/
 * </p>
 * 
 * @author dreajay
 */
public class OgnlUtils {

	private static Map<String, Object> exprCaches = new HashMap<String, Object>();

	/**
	 * ognl取值
	 * 
	 * @param ognlExpr
	 *            ognl表达式
	 * @param context
	 *            当前上下文，通常为一个Map
	 * @param root
	 *            当前对象
	 * @return
	 * @throws OgnlException
	 */
	public static Object getValue(String ognlExpr, Map context, Object root)
			throws OgnlException {
		Object expression = parseExpression(ognlExpr);
		return Ognl.getValue(expression, context, root);
	}

	/**
	 * ognl取值
	 * 
	 * @param ognlExpr
	 *            ognl表达式
	 * @param root
	 *            当前对象
	 * @return
	 * @throws OgnlException
	 */
	public static Object getValue(String ognlExpr, Object root)
			throws OgnlException {
		Object expression = parseExpression(ognlExpr);
		return Ognl.getValue(expression, root);
	}

	/**
	 * ognl设值
	 * 
	 * @param ognlExpr
	 *            ognl表达式
	 * @param context
	 *            当前上下文
	 * @param root
	 *            当前对象
	 * @param value
	 *            要设置的值
	 * @throws OgnlException
	 */
	public static void setValue(String ognlExpr, Map context, Object root,
			Object value) throws OgnlException {
		Object expression = parseExpression(ognlExpr);
		Ognl.setValue(expression, context, root, value);
	}

	/**
	 * ognl设值
	 * 
	 * @param ognlExpr
	 *            ognl表达式
	 * @param root
	 *            当前对象
	 * @param value
	 *            要设置的值
	 * @throws OgnlException
	 */
	public static void setValue(String ognlExpr, Object root, Object value)
			throws OgnlException {
		Object expression = parseExpression(ognlExpr);
		Ognl.setValue(expression, root, value);
	}

	/**
	 * ognl表达式解析
	 * 
	 * @param ognlExpr
	 * @return
	 * @throws OgnlException
	 */
	public static Object parseExpression(String ognlExpr) throws OgnlException {
		try {
			synchronized (OgnlUtils.class) {
				Object expression = exprCaches.get(ognlExpr);
				if (expression == null) {
					expression = Ognl.parseExpression(ognlExpr);
					exprCaches.put(ognlExpr, expression);
				}
				return expression;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new OgnlException(e.getMessage(), e);
		}
	}

}
