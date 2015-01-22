/**
 * 
 */
package com.jcodes.log4j.customAppender;

import java.io.Writer;

import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.spi.ErrorHandler;

public class CountingQuietWriterEx extends CountingQuietWriter {

	private String key;
	private String fileName;

	public CountingQuietWriterEx(String key, String fileName, Writer writer, ErrorHandler eh) {
		super(writer, eh);
		this.key = key;
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getGroup() {
		return key;
	}

	public String getLogDate() {
		String filePath = getFileName();
		int posit = filePath.lastIndexOf(".");
		String logDate = "";
		try {
			logDate = filePath.substring(posit-12,posit-4);
		} catch (Exception e) {
		}
		return logDate;
	}
}
