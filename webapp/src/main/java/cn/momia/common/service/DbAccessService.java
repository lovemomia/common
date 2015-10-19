package cn.momia.common.service;

import cn.momia.common.reload.Reloadable;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DbAccessService extends Reloadable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbAccessService.class);

    private Map<String, Map<String, Method>> classSetterMethods = new HashMap<String, Map<String, Method>>();

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

    public static class ObjectResultSetExtractor<T> implements ResultSetExtractor<T> {
        private Function<ResultSet, T> func;
        private T defaultValue;

        public ObjectResultSetExtractor(Function<ResultSet, T> func, T defaultValue) {
            this.func = func;
            this.defaultValue = defaultValue;
        }

        @Override
        public T extractData(ResultSet rs) throws SQLException, DataAccessException {
            return rs.next() ? func.apply(rs) : defaultValue;
        }
    }

    public static class ListResultSetExtractor<T extends Entity> implements RowCallbackHandler {
        private List<T> list;
        private Function<ResultSet, T> func;

        public ListResultSetExtractor(List<T> list, Function<ResultSet, T> func) {
            this.list = list;
            this.func = func;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            T t = func.apply(rs);
            if (t.exists()) list.add(t);
        }
    }

    public static class LongListResultSetExtractor implements RowCallbackHandler {
        private List<Long> list;

        public LongListResultSetExtractor(List<Long> list) {
            this.list = list;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            list.add(rs.getLong(1));
        }
    }

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

    public <T> T queryObject(String sql, Object[] args, Class<T> clazz, T defaultValue) {
        try {
            return jdbcTemplate.queryForObject(sql, args, clazz);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public <T> List<T> queryList(String sql, Class<T> clazz) {
        return queryList(sql, new Object[] {}, clazz);
    }

    public <T> List<T> queryList(String sql, Object[] args, Class<T> clazz) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args);
        List<T> result = new ArrayList<T>();
        for (Map<String, Object> row : list) {
            try {
                T t = clazz.newInstance();
                Map<String, Method> methods = getSetterMethods(clazz);
                for (Map.Entry<String, Method> entry : methods.entrySet()) {
                    String fieldName = entry.getKey();
                    Method method = entry.getValue();
                    Object fieldValue = row.get(fieldName);
                    if (fieldValue != null) method.invoke(t, fieldValue);
                }

                result.add(t);
            } catch (Exception e) {
                LOGGER.error("invalid row value: {}", row, e);
            }
        }

        return result;
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
                        if (methodName.length() > 3 && methodName.startsWith("set") && method.getParameterCount() == 1) methods.put(methodName.substring(3), method);
                    }

                    classSetterMethods.put(clazz.getName(), methods);
                }
            }
        }

        return methods;
    }
}
