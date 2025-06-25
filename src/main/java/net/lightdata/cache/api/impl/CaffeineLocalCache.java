package net.lightdata.cache.api.impl;

import net.lightdata.cache.api.LocalCacheApi;
import net.lightdata.cache.expiry.EntryExpiry;
import net.lightdata.cache.model.LocalCacheConfig;
import net.lightdata.cache.weight.EntryWeight;
import net.lightdata.cache.custome.CustomCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程安全说明：{@link com.github.benmanes.caffeine.cache.Cache} 本身是线程安全的，因此本类实例可以在多线程环境下安全复用。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class CaffeineLocalCache implements LocalCacheApi {
    //定义日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(CaffeineLocalCache.class);
    private final CustomCache cache;
    private final static ThreadPoolExecutor executorPool = new ThreadPoolExecutor(8,32,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));
    public CaffeineLocalCache(LocalCacheConfig config){
        logger.info("构造CaffeineLocalCache：{}", config.toString());
        this.cache = new CustomCache.CustomCacheBuilder<>()
                         .maximumWeight(config.getMaxMemorySize())
                        .expiry(new EntryExpiry<>())
                        .weigher(new EntryWeight<>(config.getWeightMemoryFactor())).executor(executorPool).build();

    }

    @Override
    public <T> T get(String key) {
        return (T)cache.get(key);
    }

    @Override
    public <T> void put(String key, T object, int expireTime) {
        cache.put(key,object, Duration.ofSeconds(expireTime));
    }

    @Override
    public  <T> T  remove(String key) {
        return (T)cache.remove(key);
    }

}
