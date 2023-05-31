package com.rapchen.sanguosha.core.common;

import java.io.Closeable;
import java.util.HashMap;

/**
 * 额外字段对象，存储一些不常用的字段，通常是给AI做判断用的
 * @author Chen Runwen
 * @time 2023/5/10 12:33
 */
public class Fields extends HashMap<String, Object> {
    public TmpField tmpField(String key, Object value) {
        put(key, value);
        return new TmpField(key);
    }

    public class TmpField implements Closeable {
        private final String key;

        public TmpField(String key) {
            this.key = key;
        }

        @Override
        public void close() {
            remove(key);
        }
    }

    /** 添加字段，支持链式编程 */
    public Fields with(String key, Object value) {
        put(key, value);
        return this;
    }

    /** 加 */
    public void incr(String key, int num) {
        Object value = get(key);
        if (value == null) put(key, num);
        else if (value instanceof Integer i) {
            put(key, i+num);
        } else {
            throw new IllegalArgumentException(
                    String.format("incr字段 %s=%s 类型错误: %s", key, value, value.getClass()));
        }
    }

    public int getInt(String key, int defaultValue) {
        Object value = get(key);
        if (value == null) return defaultValue;
        else if (value instanceof Integer i) {
            return i;
        } else {
            throw new IllegalArgumentException(
                    String.format("getInt字段 %s=%s 类型错误: %s", key, value, value.getClass()));
        }
    }

    /** 插入二级字段 */
    public void put(String key, String subKey, Object value) {
        if (!containsKey(key)) {
            put(key, new Fields());
        }
        Fields subFields = (Fields) get(key);
        subFields.put(subKey, value);
    }

    /** 获取二级字段 */
    public Object get(String key, String subKey) {
        Fields subFields = (Fields) get(key);
        if (subFields == null) return null;
        return subFields.get(subKey);
    }

    /** 删除二级字段 */
    public Object remove(String key, String subKey) {
        Fields subFields = (Fields) get(key);
        if (subFields == null) return null;
        return subFields.remove(subKey);
    }
}
