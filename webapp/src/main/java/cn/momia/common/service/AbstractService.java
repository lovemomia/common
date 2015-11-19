package cn.momia.common.service;

import cn.momia.common.reload.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractService extends Reloadable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

    private static Map<String, Map<String, Method>> classSetterMethods = new HashMap<String, Map<String, Method>>();

    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected void doReload() {}

    public int queryInt(String sql) {
        return queryInt(sql, null);
    }

    public int queryInt(String sql, Object[] args) {
        List<Integer> results = queryIntList(sql, args);
        return results.isEmpty() ? 0 : results.get(0);
    }

    public List<Integer> queryIntList(String sql) {
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    public List<Integer> queryIntList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, Integer.class);
    }

    public long queryLong(String sql) {
        return queryLong(sql, null);
    }

    public long queryLong(String sql, Object[] args) {
        List<Long> results = queryLongList(sql, args);
        return results.isEmpty() ? 0 : results.get(0);
    }

    public List<Long> queryLongList(String sql) {
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    public List<Long> queryLongList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, Long.class);
    }

    public String queryString(String sql, Object[] args, String defaultValue) {
        List<String> results = queryStringList(sql, args);
        return results.isEmpty() ? defaultValue : results.get(0);
    }

    public List<String> queryStringList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, String.class);
    }

    public Date queryDate(String sql, Object[] args, Date defaultValue) {
        List<Date> results = queryDateList(sql, args);
        return results.isEmpty() ? defaultValue : results.get(0);
    }

    public List<Date> queryDateList(String sql, Object[] args) {
        return jdbcTemplate.queryForList(sql, args, Date.class);
    }

    public <T> T queryObject(String sql, Class<T> clazz, T defaultValue) {
        return queryObject(sql, null, clazz, defaultValue);
    }

    public <T> T queryObject(String sql, Object[] args, Class<T> clazz, T defaultValue) {
        List<T> objects = queryObjectList(sql, args, clazz);
        return objects.isEmpty() ? defaultValue : objects.get(0);
    }

    public <T> List<T> queryObjectList(String sql, Class<T> clazz) {
        return queryObjectList(sql, null, clazz);
    }

    public <T> List<T> queryObjectList(String sql, Object[] args, Class<T> clazz) {
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

    protected void query(String sql, RowCallbackHandler handler) {
        query(sql, null, handler);
    }

    protected void query(String sql, Object[] args, RowCallbackHandler handler) {
        jdbcTemplate.query(sql, args, handler);
    }

    protected KeyHolder insert(PreparedStatementCreator psc) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(psc, keyHolder);

        return keyHolder;
    }

    protected boolean update(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args) >= 1;
    }

    protected int singleUpdate(String sql, Object[] args) {
        return jdbcTemplate.update(sql, args);
    }

    protected int[] batchUpdate(String sql, List<Object[]> argsList) {
        return jdbcTemplate.batchUpdate(sql, argsList);
    }

    protected void execute(TransactionCallback callback) {
        transactionTemplate.execute(callback);
    }
}
