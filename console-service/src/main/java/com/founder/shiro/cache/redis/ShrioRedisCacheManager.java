package com.founder.shiro.cache.redis;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

public class ShrioRedisCacheManager extends AbstractCacheManager {

	private RedisTemplate<byte[], byte[]> shiroRedisTemplate;

	public ShrioRedisCacheManager(RedisTemplate<byte[], byte[]> shiroRedisTemplate) {
		this.shiroRedisTemplate = shiroRedisTemplate;
	}

	@Override
	public Cache<byte[], byte[]> createCache(String name) throws CacheException {
		return new ShrioRedisCache<byte[], byte[]>(shiroRedisTemplate, name);
	}
}
