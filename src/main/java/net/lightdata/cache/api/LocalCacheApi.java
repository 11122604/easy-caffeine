package net.lightdata.cache.api;

/**
 * 本地缓存操作类Api
 * @author 1053459255@qq.com
 * @date 2025-6-22
 */
public interface LocalCacheApi {
    /**
     *
     * @param key 缓存key
     * @return 缓存对象
     * @param <T>
     */
    <T> T get(String key);

    /**
     *
     * @param key 缓存key
     * @param object 缓存对象
     * @param expireTime  过期时间 单位（秒）
     * @param <T>
     */
    <T> void put(String key,T object,int expireTime);

    /**
     *
     * @param key 缓存key
     * @return 已删除的旧缓存对象
     * @param <T>
     */
    <T> T  remove(String key);

}
