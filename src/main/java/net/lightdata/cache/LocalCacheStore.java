package net.lightdata.cache;

import net.lightdata.cache.api.LocalCacheApi;
import net.lightdata.cache.model.LocalCacheConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lightdata.cache.api.impl.CaffeineLocalCache;
import net.lightdata.cache.weight.JacksonMemoryEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *   <li>提供 get/put/remove 静态访问入口。</li>
 * </ul>
 * 调用示例：
 * <pre>
 *   LocalCacheStore store = LocalCacheStore.getStore();
 *   store.put("k", obj, 60);
 *   Object v = store.get("k");
 * </pre>
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class LocalCacheStore {
    private static final Logger logger = LoggerFactory.getLogger(LocalCacheStore.class);
    private LocalCacheApi localCache;
    private static LocalCacheStore localStore;

    static {
        //初始化预热
        LocalCacheStore.getStore();
    }
    private LocalCacheStore(){

    }
    private LocalCacheStore(LocalCacheApi localCache){
        this.localCache = localCache;
    }
    /**
     *
     * @param key 缓存key
     * @return 缓存对象
     * @param <T>
     */
    public <T> T get(String key){
        return (T)localCache.get(key);
    }
    /**
     *
     * @param key 缓存key
     * @param object 缓存对象
     * @param expireTime 过期时间 单位秒
     */
    public <T> void put(String key,T object,int expireTime){
        localCache.put(key,object,expireTime);
    }
    /**
     *
     * @param key 缓存key
     * @return
     * @param <T>
     */
    public <T> T  remove(String key){
        T obj = (T)localCache.remove(key);
        return obj;
    }
    /**
     * 双重检查锁的单例模式
     * @return
     */
    public static LocalCacheStore getStore(){
        if(localStore == null){
            synchronized (LocalCacheStore.class){
                if(localStore == null){
                    LocalCacheApi localCache;
                    try {
                        LocalCacheConfig config = loadConfig();
                        localCache = createLocalCache(config);
                        localStore = new LocalCacheStore(localCache);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("获取配置失败，创建默认配置LocalCache",e);
                        localCache = createDefault();
                        localStore = new LocalCacheStore(localCache);
                    }
                }
            }
        }
        return localStore;
    }
    /**
     * 创建默认配置的LocalCache
     * @return
     */
    private static LocalCacheApi createDefault() {
        LocalCacheConfig config = new LocalCacheConfig();
        config.setMaxMemorySize(500L * 1024L * 1024L);
        config.setWeightMemoryFactor(3);
        return new CaffeineLocalCache(config);
    }
    /**
     * 根据配置创建LocalCacheApi
     * @param config
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static LocalCacheApi createLocalCache(LocalCacheConfig config) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class stroeClass = Thread.currentThread().getContextClassLoader().loadClass(config.getClassName());
        Constructor<LocalCacheApi> constructor = stroeClass.getConstructor(LocalCacheConfig.class);
        LocalCacheApi localCache = constructor.newInstance(config);
        return localCache;
    }
    /**
     * 载入配置文件
     * @return
     * @throws IOException
     */
    private static LocalCacheConfig loadConfig() throws IOException {
        // 使用系统类加载器获取资源流
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("LocalCacheStoreConfig.json");
        // 如果未找到，尝试上下文类加载器
        if (inputStream == null) {
            inputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("LocalCacheStoreConfig.json");
        }
        // 如果仍未找到，抛出异常
        if (inputStream == null) {
            throw new IOException("配置文件未找到: LocalCacheStoreConfig.json");
        }
        // 使用Jackson解析JSON
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream,LocalCacheConfig.class);
    }

}
