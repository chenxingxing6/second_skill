package com.lxh.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RedisService {
	@Autowired
	JedisPool jedisPool;
	private static final String STATUS_CODE = "OK";

	/**
	 * 获取当个对象
	 * */
	public <T> T get(KeyPrefix prefix, String key,  Class<T> clazz) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 //生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			 String  str = jedis.get(realKey);
			 T t =  stringToBean(str, clazz);
			 return t;
		 }finally {
			  returnToPool(jedis);
		 }
	}

	public <T> T get(String key,  Class<T> clazz) {
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			//生成真正的key
			String  str = jedis.get(key);
			T t =  stringToBean(str, clazz);
			return t;
		}finally {
			returnToPool(jedis);
		}
	}

	public  Long expice(KeyPrefix prefix,String key,int exTime){
		Jedis jedis = null;
		Long result = null;
		try {
			jedis =  jedisPool.getResource();
			result = jedis.expire(prefix.getPrefix()+key,exTime);
			return result;
		} finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 设置对象
	 * */
	public <T> boolean set(KeyPrefix prefix, String key,  T value ,int exTime) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			 String str = beanToString(value);
			 if(str == null || str.length() <= 0) {
				 return false;
			 }
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			 if(exTime == 0) {
			 	 //直接保存
				 jedis.set(realKey, str);
			 }else {
			 	 //设置过期时间
				 jedis.setex(realKey, exTime, str);
			 }
			 return true;
		 }finally {
			  returnToPool(jedis);
		 }
	}

	public Long setnx(String key, int expireSecond, String value) {
		Long result = Long.valueOf(0);
		Jedis jedis = jedisPool.getResource();
		try {
			if (expireSecond == -1) {
				result = jedis.setnx(key, value);
			} else {
				String statusCode = jedis.set(key, value, "NX", "EX", expireSecond);
				// 正确状态
				if (null != statusCode && statusCode.equals(STATUS_CODE)) {
					result = 1l;
				} else {
					result = 0l;
				}
			}
		} finally {
			returnToPool(jedis);
		}

		return result;
	}


	public  Long del(KeyPrefix prefix,String key){
		Jedis jedis = null;
		Long result = null;
		try {
			jedis =  jedisPool.getResource();
			result = jedis.del(prefix.getPrefix()+key);
			return result;
		} finally {
			returnToPool(jedis);
		}
	}

	public Long del(String key){
		Jedis jedis = null;
		Long result = null;
		try {
			jedis =  jedisPool.getResource();
			result = jedis.del(key);
			return result;
		} finally {
			returnToPool(jedis);
		}
	}


	/**
	 * 判断key是否存在
	 * */
	public <T> boolean exists(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.exists(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}

	/**
	 * 增加值
	 * */
	public <T> Long incr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.incr(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}

	/**
	 * 减少值
	 * */
	public <T> Long decr(KeyPrefix prefix, String key) {
		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedis.decr(realKey);
		 }finally {
			  returnToPool(jedis);
		 }
	}

	/**
	 * 有序存储object
	 *
	 * @param key
	 * @param score
	 * @param value
	 * @param expireSecond
	 * @return 1:成功 0:不成功
	 */
	public int zadd(String key, double score, String value, int expireSecond) {
		int ret = 0;
		if (null == value || null == key) return ret;
		Jedis jedis = jedisPool.getResource();
		try {
			ret = jedis.zadd(key, score, value).intValue();
			if (expireSecond != -1) {
				jedis.expire(key.getBytes(), expireSecond);
			}
		} finally {
			returnToPool(jedis);
		}
		return ret;
	}

	public List<String> zrange(String key, long start, long end, int isAsc) {
		if (null == key) return null;
		List<String> values = new ArrayList<>();
		Jedis jedis = jedisPool.getResource();

		try {
			Set<String> a = null;
			if (isAsc == 1) {
				a = jedis.zrange(key, start, end);
			} else {
				a = jedis.zrevrange(key, start, end);
			}
			for (String a1 : a) {
				values.add(a1);
			}
		} finally {
			returnToPool(jedis);
		}
		return values;
	}

	/**
	 * bean 转 String
	 * @param value
	 * @param <T>
	 * @return
	 */
	public static <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			 return ""+value;
		}else if(clazz == String.class) {
			 return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}


	/**
	 * string转bean
	 * @param str
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			 return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			 return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			 return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		 if(jedis != null) {
			 jedis.close();
		 }
	}

}
