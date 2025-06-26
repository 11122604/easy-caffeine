package net.lightdata.cache.weight;

/**
 * 自定义OutputStream实现，用来统计字节数
 * @author 1053459255@qq.com
 * @since 2025-06-22
 */
public final class CountingOutputStream extends java.io.OutputStream {
    private int count;
    @Override public void write(int b) { count++; }
    @Override public void write(byte[] b, int off, int len) { count += len; }
    public int size() { return count; }
}