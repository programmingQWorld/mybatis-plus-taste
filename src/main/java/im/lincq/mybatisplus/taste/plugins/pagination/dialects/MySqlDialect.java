package im.lincq.mybatisplus.taste.plugins.pagination.dialects;

import im.lincq.mybatisplus.taste.plugins.pagination.IDialect;

/**
 * MYSQL 数据库分页语句组装实现 ("LIMIT offset, limit")
 */
public class MySqlDialect implements IDialect {
    @Override
    public String buildPaginationSql(String originalSql, int offset, int limit) {
        // return originalSql + "limit " + offset + "," + limit;
        StringBuilder sql = new StringBuilder(originalSql.replace(";", ""));
        sql.append(" limit ").append(offset).append(",").append(limit);
        return sql.toString();
    }
}
