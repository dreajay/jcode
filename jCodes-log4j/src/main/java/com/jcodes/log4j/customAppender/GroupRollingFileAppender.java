/**
 * 
 */
package com.jcodes.log4j.customAppender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 日志文件附加器，相比RollFileAppender的不同点在于.<br>
 * 1、日志文件支持多个，通过组名来将日志文件单独存放，group组名来自MDC当前线程上下文中<br>
 * 2、添加文件路径Path的参数，去除了File参数，日志文件按组名分文件夹并在Path下存放<br>
 * 3、去除了RollFileAppender中一些不常用的选项，如bufferedIO、bufferedSize<br>
 * 
 * log4j.appender.file=com.sunline.flow.base.ext.log4j.GroupRollingFileAppender<br>
 * log4j.appender.file.Path=d:/log/<br>
 * log4j.appender.file.bufferedIO=false<br>
 * log4j.appender.file.bufferSize=8192<br>
 * log4j.appender.file.MaxFileSize=10mb<br>
 * log4j.appender.file.maxBackupDays=5<br>
 * log4j.appender.file.layout=org.apache.log4j.PatternLayout<br>
 * log4j.appender.file.layout.ConversionPattern=[%-23d{yyyy-MM-dd
 * HH:mm:ss.S}][%-5p][%x][%t]%n[%l]%n%m%n<br>
 * 
 * @author humy
 * 
 */
public class GroupRollingFileAppender extends AppenderSkeleton implements
		MDCConstants {

	/** 日志保存路径 */
	protected String path;
	/** 日志文件最大大小，超过该大小文件将被备份和新建文件 */
	protected long maxFileSize = 10 * 1024 * 1024;
	/** 最大备份天数 */
	protected int maxBackupDays = 5;
	/** append日志信息时,先append到buffer中 */
	protected boolean bufferedIO = false;
	/** bufferedIO为true时的buffer大小,默认为8kb */
	protected int bufferSize = 8 * 1024;
	private String formatPattern = "yyyyMMdd";
	private SimpleDateFormat format = new SimpleDateFormat(formatPattern);
	private Map<String, CountingQuietWriterEx> writers = new HashMap<String, CountingQuietWriterEx>();

	/** 只开启一个线程对日志进行删除操作 */
	private ExecutorService executor = Executors.newFixedThreadPool(1);

	public GroupRollingFileAppender() {

	}

	@Override
	public void activateOptions() {

	}

	/**
	 * 关闭appender,即关闭其中所有的writer
	 */
	@Override
	public synchronized void close() {
		if (this.closed)
			return;
		this.closed = true;
		for (Map.Entry<String, CountingQuietWriterEx> en : writers.entrySet()) {
			try {
				en.getValue().close();
			} catch (IOException e) {
				LogLog.error("关闭日志文件Writer失败：" + en.getKey(), e);
			}
		}
		writers.clear();
		executor.shutdown();
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	// 无须设置同步，因为AppenderSkeleton的doAppend方法已经是同步
	@Override
	protected void append(LoggingEvent event) {
		String group = (String) event.getMDC(GROUP_KEY);
		if ("".equals(group))
			group = DEFALUT_GROUP_NAME;
		if (!checkEntryConditions())
			return;
		try {
			CountingQuietWriterEx qw = writers.get(group);
			if (qw == null) {
				qw = addCQWriter(group, getMaxLogBlockIndex(group), true);
			}
			if (qw != null)
				subAppend(qw, event);
		} catch (IOException e) {
			LogLog.error("写日志文件失败：" + group, e);
		}
	}

	protected boolean checkEntryConditions() {
		if (this.layout == null) {
			errorHandler.error("No layout set for the appender named [" + name
					+ "].");
			return false;
		}
		return true;
	}

	@SuppressWarnings("resource")
	protected void subAppend(CountingQuietWriterEx qw, LoggingEvent event)
			throws IOException {

		// 此句不加可以节省20%的资源，但程序退出时日志会缺少
		if (!bufferedIO)
			qw.flush();

		// 超过大小备份日志文件，并创建新文件
		if (qw != null) {
			long size = ((CountingQuietWriter) qw).getCount();
			if (size >= maxFileSize) {// 超过单个日志大小
				final String fileName = qw.getFileName();
				String idx = fileName.substring(fileName.lastIndexOf(".") + 1);
				qw = rollOver(qw, Integer.parseInt(idx) + 1);
			} else {
				String logDate = qw.getLogDate();
				String today = format.format(new Date(event.timeStamp));
				if (!logDate.equals("") && !logDate.equals(today)) {// 日切情况
					qw = rollOver(qw, 0);
				}
			}
		}

		// 写日志，一定要调用write(string)方法，因为只有它才会累加count
		qw.write(this.layout.format(event));
		if (layout.ignoresThrowable()) {
			// layout中忽略异常的处理，由此处写异常信息
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				int len = s.length;
				for (int i = 0; i < len; i++) {
					qw.write(s[i]);
					qw.write(Layout.LINE_SEP);
				}
			}
		}

	}

	// 备份旧日志文件，建新日志文件
	public CountingQuietWriterEx rollOver(CountingQuietWriterEx qw,
			int logBlockIndex) {
		final String fileName = qw.getFileName();
		String group = qw.getGroup();
		long size = ((CountingQuietWriterEx) qw).getCount();
		LogLog.debug("rolling over count=" + size);

		// String lastStr = fileName.substring(fileName.lastIndexOf(".") + 1);
		// String firstName = fileName.substring(0, fileName.lastIndexOf("."));

		// 取过期时间，然后删除过期时间的日志
		String logDate = qw.getLogDate();
		final Date expiredDate = getExpiredDate(logDate);

		executor.execute(new Runnable() {
			@Override
			public void run() {
				deleteExpiredLog(expiredDate, fileName);
			}
		});

		/*
		 * int logBlockIndex = 0; try { logBlockIndex =
		 * Integer.parseInt(lastStr); } catch (Exception e) {
		 * 
		 * }
		 */

		/*
		 * File file; while (true) { ++logBlockIndex; file = new File(date +
		 * ".log." + logBlockIndex); if (file.exists() && file.length() <
		 * maxFileSize) { break; } if (!file.exists()) { break; } }
		 */

		this.closeCQWriter(qw); // keep windows happy.

		try {
			qw = addCQWriter(group, logBlockIndex, true);
			// nextRollover = 0;
		} catch (IOException e) {
			LogLog.error("addCQWriter(" + group + ", true) call failed.", e);
		}
		return qw;
	}

	/**
	 * 取得过期时间
	 * 
	 * @param currentDate
	 * @return
	 */
	private Date getExpiredDate(String currentDate) {
		Calendar calendar = Calendar.getInstance();
		try {
			if (currentDate != null && !currentDate.trim().equals("")) {
				calendar.setTime(format.parse(currentDate));
			} else {
				calendar.setTime(new Date());
			}
		} catch (Exception e) {
			calendar.setTime(new Date());
		} finally {
			calendar.add(Calendar.DAY_OF_MONTH, -maxBackupDays);
		}
		return calendar.getTime();
	}

	/**
	 * 删除过期日志
	 * 
	 * @param expiredDate
	 * @param filePath
	 */
	private void deleteExpiredLog(Date expiredDate, String filePath) {
		final SimpleDateFormat format = new SimpleDateFormat(formatPattern);

		File parentFile = new File(filePath).getParentFile();
		File[] files = parentFile.listFiles(new LogFilenameFilter());
		for (File file : files) {
			try {
				String fileName = file.getName();
				String fileNameDate = fileName.substring(0,
						fileName.indexOf("."));
				Date fnDate = format.parse(fileNameDate);
				if (expiredDate != null
						&& (fnDate.getTime() < expiredDate.getTime())
						&& file.exists()) {
					file.delete();
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	// 创建一个CQWriter
	// 调用处已经是synchronized，故此处不再声明
	private/* synchronized */CountingQuietWriterEx addCQWriter(String group,
			int logBlockIndex, boolean append) throws IOException {
		CountingQuietWriterEx writer = null;
		String fileName = getLogFileFullPath(group, logBlockIndex);
		FileOutputStream ostream = null;
		try {
			ostream = new FileOutputStream(fileName, append);
		} catch (FileNotFoundException ex) {
			String parentName = new File(fileName).getParent();
			if (parentName != null) {
				File parentDir = new File(parentName);
				if (!parentDir.exists() && parentDir.mkdirs()) {
					ostream = new FileOutputStream(fileName, append);
				} else {
					throw ex;
				}
			} else {
				throw ex;
			}
		}
		Writer fw = new OutputStreamWriter(ostream);
		if (bufferedIO)
			fw = new BufferedWriter(fw, bufferSize);
		writer = new CountingQuietWriterEx(group, fileName, fw, errorHandler);
		if (append) {
			// 文件追加，则设置原文件大小
			File f = new File(fileName);
			writer.setCount(f.length());
		}
		writers.put(group, writer);
		return writer;
	}

	/**
	 * 关闭gw
	 */
	private/* synchronized */void closeCQWriter(CountingQuietWriterEx qw) {
		if (qw != null) {
			try {
				qw.close();
				writers.remove(qw.getGroup());
			} catch (java.io.IOException e) {
				// Exceptionally, it does not make sense to delegate to an
				// ErrorHandler. Since a closed appender is basically dead.
				LogLog.error("Could not close " + qw, e);
			}
		}
	}

	private int getMaxLogBlockIndex(String group) {
		int max = 0, temp = 0;
		File parent = new File(path + "/" + group + "/");
		File[] files = parent.listFiles(new LogFilenameFilter());
		if (files == null)
			return 0;
		for (File file : files) {
			try {
				String fileName = file.getName();
				String index = fileName
						.substring(fileName.lastIndexOf(".") + 1);
				temp = Integer.parseInt(index);
				if (temp > max)
					max = temp;
			} catch (Exception e) {
				continue;
			}
		}
		return max;
	}

	private String getLogFileFullPath(String group, int logBlockIndex) {
		String date = format.format(new Date());
		if (!path.endsWith("/") && !path.endsWith("\\"))
			path += "/";
		return path + group + "/" + date + ".log." + logBlockIndex;
	}

	// ~ properties getter and setter
	public String getPath() {
		return path;
	}

	public void setPath(String file) {
		this.path = file;
	}

	public String getMaxFileSize() {
		return Long.toString(maxFileSize);
	}

	public void setMaxFileSize(String value) {
		this.maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
	}

	public void setBufferedIO(boolean bufferedIO) {
		this.bufferedIO = bufferedIO;
	}

	public boolean getBufferedIO() {
		return this.bufferedIO;
	}

	public void setBufferSize(int value) {
		this.bufferSize = value;
	}

	public int getBufferSize() {
		return this.bufferSize;
	}

	public int getMaxBackupDays() {
		return maxBackupDays;
	}

	public void setMaxBackupDays(int maxBackupDays) {
		this.maxBackupDays = maxBackupDays;
	}

}

class LogFilenameFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		String pattern = "\\d{4}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\\.log.\\d+";
		return name.matches(pattern);
	}
}