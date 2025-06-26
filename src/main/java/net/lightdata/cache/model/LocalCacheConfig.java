package net.lightdata.cache.model;

/**
 * 通过对象映射便于在运行时灵活装载配置，并传递给具体的缓存实现。
 *
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public class LocalCacheConfig {
    private long maxMemorySize;
    private double weightMemoryFactor;
    private String className;

    public double getWeightMemoryFactor() {
        return weightMemoryFactor;
    }

    public void setWeightMemoryFactor(double weightMemoryFactor) {
        this.weightMemoryFactor = weightMemoryFactor;
    }

    public long getMaxMemorySize() {
        return maxMemorySize;
    }
    public void setMaxMemorySize(long maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "LocalCacheConfig{" +
                "maxMemorySize=" + maxMemorySize +
                ", weightMemoryFactor=" + weightMemoryFactor +
                ", className='" + className + '\'' +
                '}';
    }
}
