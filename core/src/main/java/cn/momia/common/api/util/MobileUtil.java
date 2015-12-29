package cn.momia.common.api.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class MobileUtil {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[0-9]{10}$");

    public static boolean isInvalid(String mobile) {
        if (StringUtils.isBlank(mobile)) return true;
        return !MOBILE_PATTERN.matcher(mobile).find();
    }

    public static String encrypt(String mobile) {
        if (isInvalid(mobile)) return "";
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
