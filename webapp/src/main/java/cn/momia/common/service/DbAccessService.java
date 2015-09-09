package cn.momia.common.service;

import cn.momia.common.reload.Reloadable;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class DbAccessService extends Reloadable {
    protected JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doReload() {}
}
