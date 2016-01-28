package cn.momia.common.service;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

    private static Map<String, Map<String, Method>> classSetterMethods = new HashMap<String, Map<String, Method>>();

    private Date lastReloadTime = null;
    private int reloadIntervalMinutes = 24 * 60;

    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

    public void setReloadIntervalMinutes(int reloadIntervalMinutes) {
        this.reloadIntervalMinutes = reloadIntervalMinutes;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    protected synchronized void reload() {
        if (!isOutOfDate()) return;

        try {
            doReload();
        } catch (Exception e) {
            LOGGER.error("reload exception", e);
        } finally {
            lastReloadTime = new Date();
        }
    }

    protected boolean isOutOfDate() {
        return lastReloadTime == null || lastReloadTime.before(new Date(new Date().getTime() - reloadIntervalMinutes * 60 * 1000));
    }

    protected void doReload() {

    }

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

    public List<String> queryStringList(String sql) {
        return jdbcTemplate.queryForList(sql, String.class);
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

    public <K, V> Map<K, V> queryMap(String sql, Class<K> keyClass, Class<V> valueClass) {
        return queryMap(sql, null, keyClass, valueClass);
    }

    public <K, V> Map<K, V> queryMap(String sql, Object[] args, final Class<K> keyClass, final Class<V> valueClass) {
        final Map<K, V> map = new HashMap<K, V>();
        jdbcTemplate.query(sql, args, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                map.put(keyClass.cast(rs.getObject(1)), valueClass.cast(rs.getObject(2)));
            }
        });

        return map;
    }

    public <K, V> Map<K, List<V>> queryListMap(String sql, Class<K> keyClass, Class<V> valueClass) {
        return queryListMap(sql, null, keyClass, valueClass);
    }

    public <K, V> Map<K, List<V>> queryListMap(String sql, Object[] args, final Class<K> keyClass, final Class<V> valueClass) {
        final Map<K, List<V>> map = new HashMap<K, List<V>>();
        jdbcTemplate.query(sql, args, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                K key = keyClass.cast(rs.getObject(1));
                V value = valueClass.cast(rs.getObject(2));
                List<V> list = map.get(key);
                if (list == null) {
                    list = new ArrayList<V>();
                    map.put(key, list);
                }
                list.add(value);
            }
        });

        return map;
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
            if (fieldValue != null) {
                Class<?> paramType = method.getParameterTypes()[0];
                if (paramType == Boolean.class || paramType == boolean.class) {
                    method.invoke(t, ((Integer) fieldValue) == 1);
                } else {
                    method.invoke(t, fieldValue);
                }
            }
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
