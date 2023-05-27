package org.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存同一线程上的变量，保存获取当前用户id
 *
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
