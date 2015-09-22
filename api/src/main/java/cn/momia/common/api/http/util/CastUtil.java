package cn.momia.common.api.http.util;

import cn.momia.common.api.dto.PagedList;
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

    public static <T> PagedList<T> toPagedList(JSONObject jsonObject, Class<T> clazz) {
        PagedList<T> pagedList = new PagedList<T>();
        pagedList.setTotalCount(jsonObject.getLong("totalCount"));
        pagedList.setNextIndex(jsonObject.getInteger("nextIndex"));
        pagedList.setList(toList(jsonObject.getJSONArray("list"), clazz));

        return pagedList;
    }
}
