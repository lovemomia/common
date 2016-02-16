package cn.momia.common.core.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.Set;
import java.util.regex.Pattern;

public class MomiaUtil {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[0-9]{10}$");
    private static final Set<String> SEX = Sets.newHashSet("男", "女");

    public static boolean isInvalidMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) return true;
        return !MOBILE_PATTERN.matcher(mobile).find();
    }

    public static String encryptMobile(String mobile) {
        if (isInvalidMobile(mobile)) return "";
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    public static boolean isInvalidSex(String sex) {
        return StringUtil.isBlank(sex) || !SEX.contains(sex);
    }
}
