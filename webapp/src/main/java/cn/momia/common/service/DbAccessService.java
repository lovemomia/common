package cn.momia.common.service;

import cn.momia.common.reload.Reloadable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

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
}
