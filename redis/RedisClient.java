package com.example.boottest.demo.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author Hyman
 * @date 2016/11/6
 */
@Component
public class RedisClient {

    private static JedisPool jedisPool;

    /**
     * 用于设置开发环境下不缓存，false不取缓存
     */
    private static boolean CACHE = true;

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    /**
     * 说明：SpringBoot会根据application.properties中的配置文件，对SpringBoot整合的redis进行自动的配置。
     * 将属性文件自动注入到org.springframework.boot.autoconfigure.data.redis.RedisProperties类中。
     * <p>
     * 自动注入redis配置属性文件
     */
    @Autowired
    private RedisProperties properties;


    @PostConstruct
    public void init() {
        JedisPoolConfig config = new JedisPoolConfig();
        //设置最大连接数
        config.setMaxTotal(properties.getJedis().getPool().getMaxActive());
        //设置最大空闲数
        config.setMaxIdle(properties.getJedis().getPool().getMaxIdle());
        //设置超时时间
        config.setMaxWaitMillis(properties.getJedis().getPool().getMaxWait().toMillis());

        //初始化连接池
        jedisPool = new JedisPool(config, properties.getHost(), properties.getPort(),
                (int) properties.getTimeout().toMillis(), properties.getPassword());
        logger.info("初始化Redis设置成功...ip:{},port:{},timeout:{},pwd:{}", properties.getHost(), properties.getPort(),
                (int) properties.getTimeout().toMillis(), properties.getPassword());
    }


    /**
     * 设置过期时间，以秒为单位
     *
     * @param key
     * @param seconds
     */
    public boolean expire(String key, int seconds) {
        if (seconds <= 0) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 1: the timeout was set. 0: the timeout was not set
            Long code = jedis.expire(key, seconds);
            return code > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);
        }
    }

    /**
     * 设置key的过期时间，它是距1970年1月1日的 00:00:00的偏移量。
     *
     * @param key
     * @param timestamp
     * @return
     */
    public boolean expireAt(String key, long timestamp) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 1: the timeout was set. 0: the timeout was not set
            Long code = jedis.expireAt(key, timestamp);
            return code > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);
        }
    }


    /**
     * 添加有过期时间的记录
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public boolean setExpire(String key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, seconds, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);
        }
    }


    /**
     * 向缓存中设置字符串内容，如果记录已存在将覆盖原有的value
     *
     * @param key   key
     * @param value value
     * @return 保存失败则返回false
     */
    public static boolean set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);
        }
    }

    /**
     * 向缓存中设置对象
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean set(String key, Object value) {
        Jedis jedis = null;
        try {
            String objectJson = JSON.toJSONString(value);
            jedis = jedisPool.getResource();
            jedis.set(key, objectJson);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);
        }
    }

    /**
     * 根据key删除缓存中的对象
     *
     * @param key
     * @return
     */
    public static boolean del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long code = jedis.del(key);
            return code > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);

        }
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     * @return
     */
    public static Object get(String key) {

        if (!CACHE) {
            return null;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Object value = jedis.get(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(jedis);
        }
    }


    /**
     * 根据key 获取对象
     *
     * @param key
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {

        if (!CACHE) {
            return null;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.get(key);
            return JSON.parseObject(value, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 根据key 判断对象是否存在
     *
     * @param key
     * @return
     */
    public static boolean exist(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(jedis);
            return false;
        }
    }


    public static boolean hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, field, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(jedis);
            return false;
        }
    }

    /**
     * 返回hash中指定存储位置的值
     *
     * @param key
     * @param field
     * @return
     */
    public static String hget(String key, String field) {

        if (!CACHE) {
            return null;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.hget(key, field);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 从hash中删除指定的存储
     *
     * @param key
     * @param field
     * @return
     */
    public static boolean hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long code = jedis.hdel(key, field);
            return code > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(jedis);
        }
    }

    /**
     * 返回hash中指定key下的所有field
     *
     * @param key
     * @return
     */
    public static Set<String> hkeys(String key) {

        if (!CACHE) {
            return null;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<String> keys = jedis.hkeys(key);
            return keys;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 关闭连接
     *
     * @param jedis
     */
    private static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

//    public static void main(String[] args) {
//
//        JSONObject jObj = new JSONObject();
//        jObj.put("key1", "第一个值");
//        jObj.put("key2", "第二个值");
//
//
//    }

}
