package com.jcodes.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * 线程池执行器工具类
 * 
 * @author humy
 * 
 */
public class ExecutorUtils {

	public static final Logger logger = Logger.getLogger(ExecutorUtils.class);
	public static final String GROUP_KEY = "GROUP_KEY";
	public static final String EXECUTE_ID_KEY = "EXECUTE_ID_KEY";
	/**
	 * 创建带日志信息的连接池.
	 * 当capacity=0时，不缓存请求直接由ThreadPool执行；
	 * 当capacity!=0时，队列缓存请求直接由ThreadPool执行，若队列数达到上限则分配新线程直到线程数达到maxSize；
	 * 
	 * @param corePoolSize 	核心线程数
	 * @param maxPoolSize 	最大线程数
	 * @param capacity 		缓存任务队列数
	 * @param aliveTime 	空闲线程存活时间
	 * @param group 		线程MDC组名
	 * @param prefix 		流水号前缀
	 * @return
	 */
	public static ExecutorService newCachedThreadPool(int corePoolSize, int maxPoolSize,
			int capacity, int aliveTime, String group, final String prefix) {
		if (StringUtils.isEmpty(group))
			group = "sys";
		final String gp = group;
		BlockingQueue<Runnable> bq = null;
		if (capacity == 0)
			bq = new SynchronousQueue<Runnable>();
		else
			bq = new LinkedBlockingQueue<Runnable>(capacity);
		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, aliveTime, TimeUnit.SECONDS, bq, new NamedThreadFactory(group)) {
			AtomicInteger ai = new AtomicInteger(1);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");
			protected void beforeExecute(Thread t, Runnable r) {
				// 将group名称绑定到当前log4j的线程上下文中
				MDC.put(GROUP_KEY, gp);
				if (prefix != null)
					MDC.put(EXECUTE_ID_KEY,
							prefix + "_" + f.format(new Date()) + "_"
									+ ai.getAndIncrement());
			}
			
			protected void afterExecute(Runnable r, Throwable t) {
				Hashtable<?, ?> ht = MDC.getContext();
				if (ht != null)
					ht.clear();
			}
		};
	}

	/**
	 * 创建带日志信息的调度线程池。
	 * @param corePoolSize 	核心线程数
	 * @param group			线程MDC组名
	 * @param prefix		流水号前缀
	 * @return
	 */
	public static ScheduledExecutorService newScheduledThreadPool(
			int corePoolSize, String group, final String prefix) {
		if (StringUtils.isEmpty(group))
			group = "sys";
		final String gp = group;
		return new ScheduledThreadPoolExecutor(corePoolSize, new NamedThreadFactory(group)) {
			AtomicInteger ai = new AtomicInteger(1);
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");
			protected void beforeExecute(Thread t, Runnable r) {
				// 将group名称绑定到当前log4j的线程上下文中
				MDC.put(GROUP_KEY, gp);
				if (prefix != null)
					MDC.put(EXECUTE_ID_KEY,
							prefix + "_" + f.format(new Date()) + "_"
									+ ai.getAndIncrement());
			}
			protected void afterExecute(Runnable r, Throwable t) {
				Hashtable<?, ?> ht = MDC.getContext();
				if (ht != null)
					ht.clear();
			}
		};
	}
	
	static class NamedThreadFactory implements ThreadFactory {
//        final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        public NamedThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + name +
//                          poolNumber.getAndIncrement() +
                         "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
	
	/**
	 * 等待线程池任务执行完成后关闭线程池，并等待其停止<br>
	 * 等待线程池任务执行完成最长1分钟，超过则立刻停止<br>
	 * 等待线程池停止最长1分钟，超过则立刻返回
	 * 
	 * @param pool
	 */
	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		if (pool == null)
			return;
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 立刻尝试关闭线程池<br>
	 * 等待线程池停止最长1分钟，超过则立刻返回
	 * 
	 * @param pool
	 */
	public static void shutdownNowAndAwaitTermination(ExecutorService pool) {
		if (pool == null)
			return;
		pool.shutdownNow();
		try {
			pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			// Restore the interrupted status
			Thread.currentThread().interrupt();
		}
	}

}
