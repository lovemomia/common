package cn.momia.common.api.http.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CastUtil {
    public static <T> List<T> toList(JSONArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add(JSON.toJavaObject(jsonObject, clazz));
        }

        return list;
    }
}
