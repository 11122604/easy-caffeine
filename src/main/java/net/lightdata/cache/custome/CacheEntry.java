package net.lightdata.cache.custome;

import java.time.Duration;

/**
 * 来实现"每条记录不同 TTL"功能。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class CacheEntry<V> {
    private final V value;
    private final Duration expireAfterWrite; // 存储该条目的过期时间

    public CacheEntry(V value, Duration expireAfterWrite) {
        this.value = value;
        this.expireAfterWrite = expireAfterWrite;
    }

    public V getValue() {
        return value;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }
}