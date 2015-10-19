package cn.momia.common.service;

import cn.momia.common.reload.Reloadable;
import com.google.common.base.Function;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class DbAccessService extends Reloadable {
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

    public static class LongResultSetExtractor implements ResultSetExtractor<Long> {
        @Override
        public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

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
}
