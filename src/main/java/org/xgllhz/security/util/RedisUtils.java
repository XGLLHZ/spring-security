package org.xgllhz.security.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: XGLLHZ
 * @date: 2022/8/13 21:48
 * @description: redis utils
 */
@Slf4j
@Component
public class RedisUtils {

    private static final Log logger = LogFactory.getLog(RedisUtils.class);

    /**
     * 添加 value
     * @param redisTemplate
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> boolean addValue(RedisTemplate<K, V> redisTemplate, K key, V value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            return true;
        } catch (Exception e) {
            logger.error("Adding Set<?> to redis error!", e);
            return false;
        }
    }

    /**
     * 获取 value
     * @param redisTemplate
     * @param key
     * @return
     */
    public static <K, V> Object getValue(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Getting value from redis error!", e);
            return null;
        }
    }

    /**
     * 删除 key
     * @param redisTemplate
     * @param key
     * @return
     */
    public static <K, V> boolean deleteKey(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("Failed to delete redis key!", e);
            return false;
        }
    }

    /**
     * 添加 set
     * @param redisTemplate
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @return
     */
    public static <K, V> boolean addSetValue(RedisTemplate<K, V> redisTemplate, K key, V value, long timeout, TimeUnit timeUnit) {
        try {
            long result = redisTemplate.opsForSet().add(key, value);
            if (result >= 0) {
                return RedisUtils.expire(redisTemplate, key, timeout, timeUnit);
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Adding Set<?> to redis error!", e);
            return false;
        }
    }

    /**
     * 获取 set
     * @param key
     * @return
     */
    public static <K, V> Set<V> getSetValue(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            Set<V> values = redisTemplate.opsForSet().members(key);
            return CollectionUtils.isEmpty(values) ? new HashSet<>() : values;
        } catch (Exception e) {
            logger.error("Getting Set<?> from redis error!", e);
            return new HashSet<>();
        }
    }

    /**
     * 添加 hash
     * @param redisTemplate
     * @param key
     * @param hashKey
     * @param value
     * @param timeout
     * @param timeUnit
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> boolean addHashValue(RedisTemplate<K, V> redisTemplate, K key, V hashKey, V value, long timeout, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return RedisUtils.expire(redisTemplate, key, timeout, timeUnit);
        } catch (Exception e) {
            logger.error("Adding Hash to redis error!", e);
            return false;
        }
    }

    /**
     * 获取 hash
     * @param redisTemplate
     * @param key
     * @param hashKey
     * @return
     */
    public static <K, V> Object getHashValue(RedisTemplate<K, V> redisTemplate, K key, V hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            logger.error("Getting Hash.value from redis error!", e);
            return null;
        }
    }

    /**
     * 获取 hash keys
     * @param redisTemplate
     * @param key
     * @return
     */
    public static <K, V> Set<Object> getHashKeys(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            Set<Object> keys = redisTemplate.opsForHash().keys(key);
            return CollectionUtils.isEmpty(keys) ? new HashSet<>() : keys;
        } catch (Exception e) {
            logger.error("Getting Hash.keys from redis error!", e);
            return new HashSet<>();
        }
    }

    /**
     * 删除 hash key
     * @param redisTemplate
     * @param key
     * @param hashKey
     * @return
     */
    public static <K, V> boolean deleteHashKey(RedisTemplate<K, V> redisTemplate, K key, Object hashKey) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKey) > 0;
        } catch (Exception e) {
            logger.error("Failed to delete redis hashKey!", e);
            return false;
        }
    }

    /**
     * 判断是否包含某个 key
     * @param redisTemplate
     * @param key
     * @return
     */
    public static <K, V> boolean containKey(RedisTemplate<K, V> redisTemplate, K key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("Judging contain key error!", e);
            return false;
        }
    }

    /**
     * 是否包含某个值
     * @param redisTemplate
     * @param key
     * @param value
     * @return
     */
    public static <K, V> boolean containSetValue(RedisTemplate<K, V> redisTemplate, K key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logger.error("Judging Set<?> contain value error!", e);
            return false;
        }
    }

    /**
     * 是否包含某个 key
     * @param redisTemplate
     * @param key
     * @param hashKey
     * @return
     */
    public static <K, V> boolean containHashKey(RedisTemplate<K, V> redisTemplate, K key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e){
            logger.error("Judging Hash contain key error!", e);
            return false;
        }
    }

    /**
     * 设置 key 过期时间
     * @param redisTemplate
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     * @param <K>
     * @param <V>
     */
    public static <K, V> boolean expire(RedisTemplate<K, V> redisTemplate, K key, long timeout, TimeUnit timeUnit) {
        try {
            return redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            logger.error("Failed to set expiration time!", e);
            return false;
        }
    }

    /**
     * 模糊匹配 key
     * @param key
     * @return
     */
    public static  <K, V> List<String> getListKey(RedisTemplate<K, V> redisTemplate, K key) {
        //match()：此方法，若参数为 "key"，在会匹配到所有以 "key" 开头的数据
        ScanOptions scanOptions = ScanOptions.scanOptions().match((String) key).build();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection redisConnection = Objects.requireNonNull(factory).getConnection();
        Cursor<byte[]> cursor = redisConnection.scan(scanOptions);
        List<String> list = new ArrayList<>();
        while (cursor.hasNext()) {
            list.add(new String(cursor.next()));
        }
        RedisConnectionUtils.releaseConnection(redisConnection, factory);
        return list;
    }

}
