package com.yinpai.server.utils;

import java.util.regex.Pattern;

/**
 * @program server
 * @description:
 * @author: liuzhenda
 * @create: 2020/12/26 20:29
 */

public class NumUtils {
    public static Boolean checkNumber(String str) {
        if (null == str || "" == str) {
            System.out.println("字符串为空");
        }
        return  Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();

    }
}
