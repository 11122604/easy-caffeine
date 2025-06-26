package net.lightdata.cache.weight;

import com.github.benmanes.caffeine.cache.Weigher;
import net.lightdata.cache.custome.CacheEntry;

/**
 * 权重 = (keySize + valueSize + 64B 对象头) × 参数 <code>weightMemoryFactor</code>
 *，从而可以灵活调整实际淘汰阈值与估算误差之间的平衡。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class EntryWeight <K, V> implements Weigher<K, CacheEntry<V>> {
    private double weightMemoryFactor;
    public EntryWeight(double weightMemoryFactor){
        this.weightMemoryFactor = weightMemoryFactor;
    }
    @Override
    public int weigh(K k, CacheEntry<V> vCacheEntry) {
        return Double.valueOf((JacksonMemoryEstimator.estimateMemorySizeWithFall(k)
                + JacksonMemoryEstimator.estimateMemorySizeWithFall(vCacheEntry.getValue()) + 64) * weightMemoryFactor).intValue();
    }
}
