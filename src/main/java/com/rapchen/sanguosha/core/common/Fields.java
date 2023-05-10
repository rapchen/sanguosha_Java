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


}
