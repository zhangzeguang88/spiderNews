package com.zzg.spiderNews.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisUtil {
	private static Logger logger = LoggerFactory.getLogger(RedisUtil.getClass());
	//  Redis测试服务器
	private static String ADDR;
	private static Integer PORT = 0;
	private static String AUTH;
	private static Properties properties;
	
	static {
		properties = new Properties();
		try {
			InputStream is = RedisUtil.class.getResourceAsStream("/redis.properties");
			properties.load(is);
		} catch (IOException e) {
			logger.error("加载系统配置文件memcached.properties失败" + e.getMessage());
		}
		ADDR = properties.getProperty("ADDR");
		PORT = Integer.parseInt(properties.getProperty("PORT"));
		AUTH = properties.getProperty("AUTH");
		if(StringUtils.isEmpty(AUTH))
			AUTH=null;
	}
	
	// 可用连接实例的最大数目，默认值为8；
	// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 1024;

	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 200;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;

	private static int TIMEOUT = 10000;

	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;

	private static JedisPool jedisPool = null;
	
	//指定数据库0
	private static int database = 0;
	
	/**
	 * 初始化Redis连接池
	 */
	static {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWait(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH,database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取Jedis实例
	 * @return
	 */
	private synchronized static Jedis getJedis() {
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	private static void returnJedis(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 向redis中存放键值对，超时时间为seconds，单位为秒
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public static String set(Object key , Object value , int seconds){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			byte[] valueByte = obj2ByteArray(value);
			if(seconds > 0)
				return jedis.setex(keyByte, seconds, valueByte);
			else
				return jedis.set(keyByte, valueByte);
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	public static void lpush(Object key , Object value){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			byte[] valueByte = obj2ByteArray(value);
			jedis.lpush(keyByte, valueByte);
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	public static Object rpop(Object key){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			byte[] valueByte = jedis.lpop(keyByte);
			return byteArray2Obj(valueByte);
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	public static void incr(String key){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			jedis.incr(keyByte);
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	public static long getLong(Object key){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			byte[] value = jedis.get(keyByte);
			if(value == null)
				return 0L;
			else
				return Long.parseLong(new String(value));
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	/**
	 * 向redis中存放键值对，超时时间为seconds，单位为秒
	 * @param key
	 * @param value
	 * @param seconds
	 * @return 如果key不存在并且设置成功，返回true
	 */
	public static boolean setNX(Object key , Object value , int seconds){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			byte[] valueByte = obj2ByteArray(value);
			
			Long line = jedis.setnx(keyByte,valueByte);
			if(line==null || line == 0){
				return false;
			}
			
			if(seconds > 0)
				jedis.expire(keyByte, seconds);
			
			return true;
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	/**
	 * 获取Redis中key的value值
	 * @param key
	 * @return
	 */
	public static Object get(Object key){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			byte[] result = jedis.get(keyByte);
			if(result == null)
				return null;
			else
				return byteArray2Obj(result);
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	

	/**
	 * 获取Redis中key的value值
	 * @param key
	 * @return
	 */
	public static List<String> get(Object key,long start,long end){
		Jedis jedis = getJedis();
		try{
			byte[] keyByte = obj2ByteArray(key);
			List<byte[]> result = jedis.lrange(keyByte, start, end);
			if(result.size() == 0)
				return null;
			else{
				List<String> list = new ArrayList<String>();
				for(int i=0;i<result.size();i++){
					list.add((String) byteArray2Obj(result.get(i)));
				}
				return list;
			}
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	/**
	 * 删除一个key
	 * @param keys
	 * @return
	 */
	public static Long del(Object key){
		Jedis jedis = getJedis();
		try{
			return jedis.del(obj2ByteArray(key));
		}catch(Exception e){
			throw e;
		}finally{
			returnJedis(jedis);
		}
	}
	
	 public static byte[] obj2ByteArray(Object object) {
	        ObjectOutputStream oos = null;
	        ByteArrayOutputStream baos = null;
	        try {
	            // 序列化
	            baos = new ByteArrayOutputStream();
	            oos = new ObjectOutputStream(baos);
	            oos.writeObject(object);
	            byte[] bytes = baos.toByteArray();
	            return bytes;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    /**
	     * 反序列化
	     *
	     * @param bytes
	     * @return
	     */
	    public static Object byteArray2Obj(byte[] bytes) {
	        ByteArrayInputStream bais = null;
	        if (bytes == null) {
	            return null;
	        }
	        try {
	            // 反序列化
	            bais = new ByteArrayInputStream(bytes);
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            return ois.readObject();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	
}
