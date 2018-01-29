package com.zzg.spiderNews.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

public final class JedisUtil {
	
	private static JedisPool pool;
	
	public final static String SUFFIX_SWITCH = "_CREDIT_SWITCH";//系统开关后缀名

    public final static int EXPIRE_5_MINUTES = 300;//300秒

    public final static int EXPIRE_1_MINUTES = 60;//60秒
	
    public static String PASSWORD = "";
    
    static {
        ResourceBundle bundle = ResourceBundle.getBundle("jedis-pool");
        if (bundle == null) {
            throw new IllegalArgumentException(
                    "[jedis-pool.properties] is not found!");
        }

        PASSWORD = bundle.getString("redis.password");

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.valueOf(bundle
                .getString("redis.pool.maxActive")));
        //config.setMaxActive(Integer.valueOf(bundle.getString("redis.pool.maxActive")));
        config.setMaxIdle(Integer.valueOf(bundle
                .getString("redis.pool.maxIdle")));
        config.setMaxWaitMillis(Long.valueOf(bundle.getString("redis.pool.maxWait")));
        config.setTestOnBorrow(Boolean.valueOf(bundle
                .getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle
                .getString("redis.pool.testOnReturn")));

        pool = new JedisPool(config, bundle.getString("redis.ip"), Integer.valueOf(bundle.getString("redis.port")), Protocol.DEFAULT_TIMEOUT, PASSWORD);
    }
    
    public static Object get(String key) {
        Jedis jedis = pool.getResource();
        Object obj = unserialize(jedis.get(SafeEncoder.encode(key)));
        pool.returnResourceObject(jedis);
        return obj;
    }

    public static void set(String key, Object value) {
        Jedis jedis = pool.getResource();
        byte[] encodeKey = SafeEncoder.encode(key);
        jedis.setex(encodeKey, EXPIRE_5_MINUTES, serialize(value));
        pool.returnResourceObject(jedis);
    }

    public static void set1(String key, Object value) {
        Jedis jedis = pool.getResource();
        byte[] encodeKey = SafeEncoder.encode(key);
        jedis.set(encodeKey, serialize(value));
        pool.returnResourceObject(jedis);
    }

    public static void set(String key, Object value, int seconds) {
        Jedis jedis = pool.getResource();
        byte[] encodeKey = SafeEncoder.encode(key);
        jedis.setex(encodeKey, seconds, serialize(value));
        pool.returnResourceObject(jedis);
    }

    public static void persist(String key, Object value) {
        Jedis jedis = pool.getResource();
        byte[] encodeKey = SafeEncoder.encode(key);
        jedis.setex(encodeKey, EXPIRE_5_MINUTES, serialize(value));
        jedis.persist(encodeKey);
        pool.returnResourceObject(jedis); 
    }

    public static boolean setnxex(String key, Object value) {
        boolean status = false;
        Jedis jedis = pool.getResource();
        byte[] encodeKey = SafeEncoder.encode(key);
        String result = jedis.set(encodeKey,serialize(value),"nx".getBytes(),"ex".getBytes(),EXPIRE_5_MINUTES);
        if("OK".equals(result)){
            status = true;
        }
        pool.returnResourceObject(jedis);
        return status;
    }

    /**
     * 重新设定时间
     *
     * @param key
     * @param seconds
     */
    public static void expire(String key, int seconds) {
        if (seconds <= 0) {
            return;
        }
        Jedis jedis = pool.getResource();
        jedis.expire(SafeEncoder.encode(key), seconds);
        pool.returnResourceObject(jedis);
    }

    public static Long del(String key) {
        Jedis jedis = pool.getResource();
        Long count = jedis.del(SafeEncoder.encode(key));
        pool.returnResourceObject(jedis);
        return count;
    }

    public static byte[] serialize(Object object) {
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
    public static Object unserialize(byte[] bytes) {
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

    public static Boolean exists(String key) {
        Jedis jedis = pool.getResource();
        Boolean existsFlag = jedis.exists(key);
        pool.returnResourceObject(jedis);
        return existsFlag;
    }

    /**
     * 获得key的剩余存活时间
     *
     * @param key
     * @return
     */
    public static long ttl(String key) {
        Jedis jedis = pool.getResource();
        long ttlTime = jedis.ttl(key);
        pool.returnResourceObject(jedis);
        return ttlTime;
    }

    public static Object scriptCall(ScriptCall call){
        Jedis jedis = pool.getResource();
        Object result = call.call(jedis);
        pool.returnResourceObject(jedis);
        return result;
    }

    public interface ScriptCall{
        Object call(Jedis jedis);
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println(JedisUtil.ttl("CREDIT_02iA0Y02De7Y00E0e0E0i0Re7De0afW0"));
            Thread.sleep(2000);
        }
    }
	
}
