package com.harry.wechat.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author Harry
 * @date 2020/9/25
 * Time: 12:14
 * Desc: MapUtils
 */
public class MapUtils {
    /**
     * Map转String工具
     * @param map
     * @param separator 分隔符
     * @param kvSplice  键值拼接符
     * @return
     */
    public static String mapToString(Map<?, ?> map, String separator, String kvSplice) {
        List<String> result = Lists.newArrayList();
        map.entrySet().parallelStream().reduce(result, (first, second)->{
            first.add(second.getKey() + kvSplice + second.getValue());
            return first;
        }, (first, second)->{
            if (first == second) {
                return first;
            }
            first.addAll(second);
            return first;
        });

        return String.join(separator,result);
    }

    public static String mapToString(Map<String, List<String>> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach( (k,v) -> {
            sb.append(k);
            sb.append(":");
            sb.append(String.join("、",v));
            sb.append(",");
        });
        return sb.toString().replaceAll(",$","");
    }

    public static String mapToValueString(Map<String, List<String>> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach( (k,v) -> {
            sb.append(String.join("、",v));
            sb.append(",");
        });
        return sb.toString().replaceAll(",$","");
    }
}
