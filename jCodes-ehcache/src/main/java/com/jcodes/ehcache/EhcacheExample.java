package com.jcodes.ehcache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhcacheExample {

	public static void main(String[] args) {
		CacheManager manager = CacheManager.create(); 
//		CacheManager manager2 = CacheManager.create("src/ehcache.xml");  
		
		URL url = EhcacheExample.class.getClass().getResource("/ehcache.xml");  
		CacheManager manager3 = CacheManager.create(url); 
		
		
		Cache cache = manager.getCache("sampleCache1"); 
		
		System.out.println(cache);
		
		//设置一个名为test 的新cache,test属性为默认
		manager.addCache("test"); 		
		
		
		//设置一个名为test 的新cache,并定义其属性
		Cache cache2 = new Cache("cache2", 1, true, false, 5, 2);  
		manager.addCache(cache2);
		
		
		//往cache中加入元素 
		Element element = new Element("key1", "value1");  
		cache2.put(element);
		
		
		// 从cache中取得元素
		Element element2 = cache2.get("key1"); 
		
		
		System.out.println(element.getValue());  
        Object obj = element.getObjectValue();  
        System.out.println((String)obj);
        
		
		manager.shutdown();
	}

}
