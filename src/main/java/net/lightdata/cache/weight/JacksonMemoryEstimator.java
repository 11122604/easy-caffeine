package net.lightdata.cache.weight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于 Jackson {@link ObjectMapper} 的快速内存大小估算工具。
 * <p>
 * 通过将对象序列化为 JSON 字节数组并返回其长度，作为对象所占内存的近似值。
 * 仅用于缓存权重等场景，不代表真实 JVM 堆占用。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class JacksonMemoryEstimator {
    private static final Logger logger = LoggerFactory.getLogger(JacksonMemoryEstimator.class);
    // 配置重用 ObjectMapper（线程安全）
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new AfterburnerModule());

    private static final ConcurrentMap<Class<?>, ObjectWriter> WRITER_CACHE = new ConcurrentHashMap<>();

    /**
     * 估算对象序列化后的字节大小（快速估算）
     *
     * @param value 待估算的对象
     * @return 序列化后的字节大小（估算值）
     * @throws IOException 序列化错误时抛出
     */
    public static int estimateMemorySize(Object value) throws IOException {
        if (value == null) {
            return 0;
        }
        ObjectWriter writer = WRITER_CACHE.computeIfAbsent(value.getClass(), MAPPER::writerFor);
        CountingOutputStream cos = new CountingOutputStream();
        writer.writeValue(cos, value);
        return cos.size();
    }

    /**
     * 创建可重用的内存估算函数（用于缓存权重计算等场景）
     *
     * @return 函数接口，可直接用于 Weigher
     */
    public static int estimateMemorySizeWithFall(Object value) {
            try {
                return estimateMemorySize(value);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("估算对象大小异常",e);
                // 回退策略：返回默认估算值
                return fallbackEstimate(value);
            }
    }

    // 基本回退估算规则
    private static int fallbackEstimate(Object value) {
        if (value == null) {
            return 0;
        }

        Class<?> clazz = value.getClass();

        // 基本类型估算
        if (clazz == String.class) {
            return ((String) value).getBytes().length;
        }
        if (clazz == Integer.class) {
            return 4;
        }
        if (clazz == Long.class) {
            return 8;
        }
        if (clazz == Double.class) {
            return 8;
        }
        if (clazz == Boolean.class) {
            return 1;
        }

        // 集合类快速估算
        if (value instanceof java.util.Collection) {
            java.util.Collection<?> col = (java.util.Collection<?>) value;
            return 40 + 8 * col.size(); // 基础大小 + 每个元素8字节估算
        }

        // 默认估算（基于字段数量）
        return clazz.getDeclaredFields().length * 20;
    }


}