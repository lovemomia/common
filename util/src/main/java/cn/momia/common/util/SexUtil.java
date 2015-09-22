package cn.momia.common.util;

import org.jsoup.helper.StringUtil;

import java.util.HashSet;
import java.util.Set;

public class SexUtil {
    private static final Set<String> SEX = new HashSet<String>();
    static {
        SEX.add("男");
        SEX.add("女");
    }

    public static boolean isInvalid(String sex) {
        return StringUtil.isBlank(sex) || !SEX.contains(sex);
    }
}
