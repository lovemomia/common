package cn.momia.common.core.util;

import cn.momia.common.core.dto.PagedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CastUtil {
    public static <T> T toObject(JSON json, Class<T> clazz) {
        return JSON.toJavaObject(json, clazz);
    }

    public static <T> List<T> toList(JSON json, Class<T> clazz) {
        JSONArray jsonArray = (JSONArray) json;

        List<T> list = new ArrayList<T>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getObject(i, clazz));
        }

        return list;
    }

    public static <T> PagedList<T> toPagedList(JSON json, Class<T> clazz) {
        JSONObject jsonObject = (JSONObject) json;

        PagedList<T> pagedList = new PagedList<T>();
        pagedList.setTotalCount(jsonObject.getLong("totalCount"));
        pagedList.setNextIndex(jsonObject.getInteger("nextIndex"));
        pagedList.setList(toList(jsonObject.getJSONArray("list"), clazz));

        return pagedList;
    }
}
