package com.jcodes.memcache;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

/**
 * http://blog.sina.com.cn/s/blog_617a491c0100fvjs.html
 * http://blog.csdn.net/sup_heaven/article/details/32337711
 *
 * @author dreajay
 */
public class AliMemCachedUtil {

   
    private static final Logger                   logger                = Logger.getLogger(AliMemCachedUtil.class);
    //默认缓存3分钟
    private static final int                      REF_SECONDS       = 3 * 60;
   
    private static ICacheManager<IMemcachedCache> manager;
   
    private static Map<String, IMemcachedCache>   cacheArray;
   
    private static final String                   defalutCacheName = "mclient1";
   
    static {
        cacheArray = new HashMap<String, IMemcachedCache>();
        manager = CacheUtil.getCacheManager(IMemcachedCache.class, MemcachedCacheManager.class
            .getName());
        // manager.setConfigFile("memcached.xml");
        manager.start();
        cacheArray.put(defalutCacheName, manager.getCache(defalutCacheName));
    }

    private static String getCacheName(String type, Object key) {
        StringBuffer cacheName = new StringBuffer(type);
        if (key != null) {
            cacheName.append("_").append(key);
        }
        return cacheName.toString();
    }
   
    public static void set(String type, Object key, Object value) {
        set(type, key, value, REF_SECONDS);
    }
   
    public static void putNoTimeInCache(String type, Object key, Object value) {
        if (value != null) {
            set(type, key, value, -1);
        }
    }

    public static void set(String type, Object key, Object value, int seconds) {
        if (value != null) {
            String cacheName = getCacheName(type, key);
            try {
                if (seconds < 1) {
                    cacheArray.get(defalutCacheName).put(cacheName, value);
                } else {
                    cacheArray.get(defalutCacheName).put(cacheName, value, seconds);
                }
            } catch (Exception e) {
                logger.log(Level.INFO, "cache " + defalutCacheName + " socket error。");
            }
        }
    }
   
    public static void delete(String type, Object key) {
        cacheArray.get(defalutCacheName).remove(getCacheName(type, key));
    }
   
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, String type, Object key) {
        return (T) cacheArray.get(defalutCacheName).get(getCacheName(type, key));
    }
   
    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(Class<T> clazz, String type, Object key) {
        return (List<T>) cacheArray.get(defalutCacheName).get(getCacheName(type, key));
    }
   
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, String type, Object key, int localTTL) {
        try {
            return (T) cacheArray.get(defalutCacheName).get(getCacheName(type, key), localTTL);
        } catch (Exception e) {
            return null;
        }
    }
   
    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(Class<T> clazz, String type, Object key, int localTTL) {
        try {
            return (List<T>) cacheArray.get(defalutCacheName)
                .get(getCacheName(type, key), localTTL);
        } catch (Exception e) {
            return null;
        }
    }
   
    @SuppressWarnings("unchecked")
    public static <V> Map<String, V> getMap(Class<V> clazz, String type, Object key, int localTTL) {
        try {
            return (Map<String, V>) cacheArray.get(defalutCacheName).get(getCacheName(type, key),
                localTTL);
        } catch (Exception e) {
            return null;
        }
    }
   
    @SuppressWarnings("unchecked")
    public static <V> Map<String, V> getMap(Class<V> clazz, String type, Object key) {
        try {
            return (Map<String, V>) cacheArray.get(defalutCacheName).get(getCacheName(type, key));
        } catch (Exception e) {
            return null;
        }
    }
   
    public static Set<String> getKeyList() {
        return cacheArray.get(defalutCacheName).keySet();
    }
   
    public static void clear() {
        cacheArray.get(defalutCacheName).clear();
    }

    public static void close() {
        manager.stop();
    }

    public static void main(String argv[]) {
        if (argv.length == 0) {
            System.out.println("Usage:MemCachedUtil get|del|set|list [type] [key] [value]");
            return;
        }
        String type = null;
        String key = null;
        String value = null;
        if ("get".equals(argv[0])) {
            if (argv.length < 3) {
                System.out.println("Usage:MemCachedUtil get type key");
                return;
            }
            type = argv[1];
            key = argv[2];
            System.out.println(AliMemCachedUtil.get(Object.class, type, key));
        } else if ("del".equals(argv[0])) {
            if (argv.length < 3) {
                System.out.println("Usage:MemCachedUtil del type key");
                return;
            }
            type = argv[1];
            key = argv[2];
            AliMemCachedUtil.delete(type, key);
        } else if ("set".equals(argv[0])) {
            if (argv.length < 4) {
                System.out.println("Usage:MemCachedUtil set type key value");
                return;
            }
            type = argv[1];
            key = argv[2];
            value = argv[3];
            AliMemCachedUtil.set(type, key, value);
        } else if ("list".equals(argv[0])) {
            System.out.println(AliMemCachedUtil.getKeyList());
        }
        AliMemCachedUtil.close();
    }
}