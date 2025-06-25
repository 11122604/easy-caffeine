package net.lightdata.cache.expiry;

import com.github.benmanes.caffeine.cache.Expiry;
import net.lightdata.cache.custome.CacheEntry;

/**
 * 创建时直接读取 {@link CacheEntry#getExpireAfterWrite()} 中保存的 {@link Duration}，
 * 更新 / 读取时保持原有过期时间不变，从而实现固定 TTL 且不续期的效果。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class EntryExpiry<K, V> implements Expiry<K, CacheEntry<V>> {

    @Override
    public long expireAfterCreate(
            K key, CacheEntry<V> value, long currentTime) {
        // 从包装对象中提取预设的过期时间
        return value.getExpireAfterWrite().toNanos();
    }

    @Override
    public long expireAfterUpdate(
            K key, CacheEntry<V> value,
            long currentTime, long currentDuration) {
        // 更新时不改变过期时间
        return currentDuration;
    }

    @Override
    public long expireAfterRead(
            K key, CacheEntry<V> value,
            long currentTime, long currentDuration) {
        // 读取时不续期
        return currentDuration;
    }
}