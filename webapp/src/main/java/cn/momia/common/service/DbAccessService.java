package cn.momia.common.service;

import cn.momia.common.reload.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DbAccessService extends Reloadable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbAccessService.class);

    private static Map<String, Map<String, Method>> classSetterMethods = new HashMap<String, Map<String, Method>>();

    protected JdbcTemplate jdbcTemplate;
    protected TransactionTemplate transactionTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected void doReload() {}

    public int queryInt(String sql, Object[] args) {
        Number number = jdbcTemplate.queryForObject(sql, args, Integer.class);
        return (number != null ? number.intValue() : 0);
    }

    public List<Integer> queryIntList(String sql) {
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    public List<Integer> queryIntList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, Integer.class);
    }

    public long queryLong(String sql, Object[] args) {
        Number number = jdbcTemplate.queryForObject(sql, args, Integer.class);
        return (number != null ? number.intValue() : 0);
    }

    public List<Long> queryLongList(String sql) {
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    public List<Long> queryLongList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, Long.class);
    }

    public List<String> queryStringList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, String.class);
    }

    public String queryString(String sql, Object[] args, String defaultValue) {
        try {
            return jdbcTemplate.queryForObject(sql, args, String.class);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Date queryDate(String sql, Object[] args, Date defaultValue) {
        try {
            return jdbcTemplate.queryForObject(sql, args, Date.class);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public <T> T queryObject(String sql, Object[] args, Class<T> clazz, T defaultValue) {
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, args);
            return buildObject(row, clazz);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private <T> T buildObject(Map<String, Object> row, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t = clazz.newInstance();
        Map<String, Method> methods = getSetterMethods(clazz);
        for (Map.Entry<String, Method> entry : methods.entrySet()) {
            String fieldName = entry.getKey();
            Method method = entry.getValue();
            Object fieldValue = row.get(fieldName);
            if (fieldValue != null) method.invoke(t, fieldValue);
        }

        return t;
    }

    public <T> List<T> queryList(String sql, Class<T> clazz) {
        return queryList(sql, new Object[] {}, clazz);
    }

    public <T> List<T> queryList(String sql, Object[] args, Class<T> clazz) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args);
        List<T> list = new ArrayList<T>();
        for (Map<String, Object> row : rows) {
            try {
                list.add(buildObject(row, clazz));
            } catch (Exception e) {
                LOGGER.error("invalid row value: {}", row, e);
            }
        }

        return list;
    }

    private <T> Map<String, Method> getSetterMethods(Class<T> clazz) {
        Map<String, Method> methods = classSetterMethods.get(clazz.getName());
        if (methods == null) {
            synchronized (this) {
                methods = classSetterMethods.get(clazz.getName());
                if (methods == null) {
                    methods = new HashMap<String, Method>();
                    Method[] allMethods = clazz.getMethods();
                    for (Method method : allMethods) {
                        String methodName = method.getName();
                        if (methodName.length() > 3 && methodName.startsWith("set") && method.getParameterTypes().length == 1) methods.put(methodName.substring(3), method);
                    }

                    classSetterMethods.put(clazz.getName(), methods);
                }
            }
        }

        return methods;
    }

    protected boolean update(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args) == 1;
    }
}
