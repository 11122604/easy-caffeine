package net.lightdata.cache.custome;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.Weigher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * 注意：该类本身线程安全，内部委托给 Caffeine 的线程安全实现。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class CustomCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(CustomCache.class);
    private final Cache<K, CacheEntry<V>> cache;
    private CustomCache(Cache<K, CacheEntry<V>> cache) {
        this.cache = cache;
    }

    public void put(K key, V value, Duration expireAfterWrite) {
        CacheEntry<V> entry = new CacheEntry<>(value, expireAfterWrite);
        cache.put(key, entry);
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.getIfPresent(key);
        return (entry != null) ? entry.getValue() : null;
    }

    public V remove(K key){
        cache.cleanUp();
        CacheEntry<V> entry = cache.getIfPresent(key);
        cache.invalidate(key);
        return entry.getValue();
    }

    public static class CustomCacheBuilder <K,V> {
        private  long maximumWeight;
        private  Expiry<? super K, ? super CacheEntry<V>> expiry;
        private  Weigher<? super K, ? super CacheEntry<V>> weigher;
        private Executor executor;
        public CustomCacheBuilder executor(Executor executor){
            this.executor = executor;
            return this;
        }
        public CustomCacheBuilder maximumWeight(long maximumWeight){
            this.maximumWeight = maximumWeight;
            return this;
        }
        public CustomCacheBuilder expiry(Expiry<? super K, ? super CacheEntry<V>> expiry){
            this.expiry = expiry;
            return this;
        }
        public CustomCacheBuilder weigher( Weigher<? super K, ? super CacheEntry<V>> weigher){
            this.weigher = weigher;
            return this;
        }
        public CustomCache build(){
            if(this.maximumWeight <=0 || this.expiry == null || this.weigher == null){
                logger.info("参数错误:maximumWeight = {},expiry = {},weigher = {}",maximumWeight,expiry,weigher);
                throw new IllegalArgumentException("参数错误");
            }
            Caffeine<K, CacheEntry<V>>  caffeineBuilder = Caffeine.newBuilder()
                    .expireAfter(expiry)
                    .maximumWeight(maximumWeight)
                    .weigher(weigher);
            if(executor != null){
                caffeineBuilder.executor(executor);
            }
            return new CustomCache(caffeineBuilder.build());
        }

    }

}