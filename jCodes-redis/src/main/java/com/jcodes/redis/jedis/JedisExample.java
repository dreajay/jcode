package com.jcodes.redis.jedis;

import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisExample {

	public static JedisPool getPool() {
		ResourceBundle bundle = ResourceBundle.getBundle("redis");
		if (bundle == null) {
			throw new IllegalArgumentException("[redis.properties] is not found!");
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(Integer.valueOf(bundle.getString("redis.pool.maxActive")));
		config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));
		config.setMaxWait(Long.valueOf(bundle.getString("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(bundle.getString("redis.pool.testOnReturn")));
		JedisPool pool = new JedisPool(config, bundle.getString("redis.ip"), Integer.valueOf(bundle.getString("redis.port")));
		return pool;
	}

	public static void main(String[] args) {
		// 直接连接
		Jedis jedis = new Jedis("127.0.0.1");
		String keys = "name";
		// 存数据
		jedis.set(keys, "snowolf");
		// 取数据
		String value = jedis.get(keys);
		System.out.println(value);

		// 删数据
		jedis.del(keys);
		//释放连接
		jedis.disconnect();
		
		// 或者从池中获取一个Jedis对象
//		JedisPool pool = getPool();
//		jedis = pool.getResource();
//		// 存数据
//		jedis.set("key1", "value1");
//		// 读数据
//		System.out.println(jedis.get("key1"));
		// 释放对象池
		// pool.returnResource(jedis);
	}

}
