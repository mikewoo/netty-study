package com.mikewoo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Json工具类
 *
 * @auther Phantom Gui
 * @date 2018/6/15 15:46
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String map2Json(Map<?, ?> map) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Map<?, ?> json2Map(String json, TypeReference<?> type) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return (Map<?, ?>)objectMapper.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
