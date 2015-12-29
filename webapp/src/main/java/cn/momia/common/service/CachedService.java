package cn.momia.common.service;

import java.util.ArrayList;
import java.util.List;

public class CachedService extends AbstractService {
    private List<?> cachedList = new ArrayList<Object>();

    private Class<?> type;
    private String sql;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    protected void doReload() {
        cachedList = queryObjectList(sql, type);
    }

    public List<?> listAll() {
        if (isOutOfDate()) reload();
        return cachedList;
    }
}
