package com.jcodes.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 工具类
 * 
 * @author zangweiren 2010-4-19
 * 
 */
public class Toolkit {
	private static long start_time;
	private static List<String> times = new ArrayList<String>();

	public static void endTime(Parser parser) {
		long end_time = System.currentTimeMillis();
		double seconds = (end_time - start_time) / 1000.0;

		StringBuilder b = new StringBuilder();
		b.append("--------------\n");
		b.append(parser.getName() + " Time used:" + seconds + " s");
		times.add(b.toString());
	}

	public static void printTimes() {
		for (Iterator<String> ts = times.iterator(); ts.hasNext();) {
			System.out.println(ts.next());
		}
		times.clear();
	}

	public static void startTime(Parser parser) {
		start_time = System.currentTimeMillis();
	}
}
