package net.lightdata.cache.api;

/**
 * 本地缓存操作类Api
 * @author 1053459255@qq.com
 * @date 2025-6-22
 */
public interface LocalCacheApi {
    /**
     *
     * @param key
     * @return
     * @param <T>
     */
    <T> T get(String key);

    /**
     *
     * @param key
     * @param object
     * @param <T>
     */
    <T> void put(String key,T object,int expireTime);

    /**
     *
     * @param key
     * @return
     * @param <T>
     */
    <T> T  remove(String key);

}
