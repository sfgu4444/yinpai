package com.yinpai.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/18 11:23 上午
 */
@Slf4j
public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJsonString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("json转化失败:" + o.toString());
            return null;
        }
    }

    public static <T> T toObject(String jsonString, Class<T> c) {
        try {
            return mapper.readValue(jsonString, c);
        } catch (JsonProcessingException e) {
            log.error("json转化失败:" + jsonString);
            return null;
        }
    }



}
