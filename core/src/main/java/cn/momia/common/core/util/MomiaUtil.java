package cn.momia.common.core.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.helper.StringUtil;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MomiaUtil {
    public static final String[] CHINESE_NUMBER_CHARACTER = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
    private static final Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[0-9]{10}$");
    private static final Set<String> SEX = Sets.newHashSet("男", "女");

    public static Collection<Integer> splitIntegers(String str) {
        List<Integer> result = new ArrayList<Integer>();
        for (String val : COMMA_SPLITTER.split(str)) {
            result.add(Integer.valueOf(val));
        }

        return result;
    }

    public static Collection<Integer> splitDistinctIntegers(String str) {
        Set<Integer> result = new HashSet<Integer>();
        for (String val : COMMA_SPLITTER.split(str)) {
            result.add(Integer.valueOf(val));
        }

        return result;
    }

    public static Collection<Long> splitLongs(String str) {
        List<Long> result = new ArrayList<Long>();
        for (String val : COMMA_SPLITTER.split(str)) {
            result.add(Long.valueOf(val));
        }

        return result;
    }

    public static Collection<Long> splitDistinctLongs(String str) {
        Set<Long> result = new HashSet<Long>();
        for (String val : COMMA_SPLITTER.split(str)) {
            result.add(Long.valueOf(val));
        }

        return result;
    }

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

    public static int distance(double lng1, double lat1, double lng2, double lat2) {
        double R = 6378137; // 地球半径

        double deltaLng = (lng1 - lng2) * Math.PI / 180.0;

        double latRadian1 = lat1 * Math.PI / 180.0;
        double latRadian2 = lat2 * Math.PI / 180.0;
        double deltaLat = latRadian1 - latRadian2;

        double sinLng = Math.sin(deltaLng / 2.0);
        double sinLat = Math.sin(deltaLat / 2.0);

        return (int) (2 * R * Math.asin(Math.sqrt(sinLat * sinLat + Math.cos(latRadian1) * Math.cos(latRadian2) * sinLng * sinLng)));
    }

    public static String mapToXml(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();

        builder.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
        }
        builder.append("</xml>");

        return builder.toString();
    }

    public static Map<String, String> xmlToMap(String xml) {
        Map<String, String> params = new HashMap<String, String>();

        try {
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(new ByteArrayInputStream(xml.trim().getBytes()));
            List<Element> elements = doc.getRootElement().elements();
            for (Element element : elements) {
                String name = element.getName();
                String value = element.getTextTrim();
                params.put(name, value);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return params;
    }
}
