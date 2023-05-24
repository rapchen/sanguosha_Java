package com.rapchen.sanguosha.core.common;

import java.util.Collection;
import java.util.Iterator;

/**
 * 工具类，提供静态方法
 * @author Chen Runwen
 * @time 2023/5/24 23:43
 */
public class Utils {

    public static String objectsToString(Collection<?> objects, boolean withNumber) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Iterator<?> iter = objects.iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            if (withNumber) {  // 带上序号
                sb.append(i).append(": ");
            }
            sb.append(o.toString());
            if (iter.hasNext()) sb.append(", ");  // 如果不是最后一个，带上逗号
            i++;
        }
        return sb.toString();
    }

}
