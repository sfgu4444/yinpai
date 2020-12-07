package com.yinpai.server.utils;

import com.yinpai.server.enums.BaseCodeEnum;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-06-15 14:19
 */
public class EnumUtils {

    public static <T extends BaseCodeEnum> T getByCode(Object code, Class<T> enumClass) {
        for (T value : enumClass.getEnumConstants()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static <D extends BaseCodeEnum> D getByMessage(Object message, Class<D> enumClass) {
        for (D value : enumClass.getEnumConstants()) {
            if (value.getMessage().equals(message)) {
                return value;
            }
        }
        return null;
    }
}
