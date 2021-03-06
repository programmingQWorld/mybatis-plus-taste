package im.lincq.mybatisplus.taste.plugins.pagination.dialects;

import im.lincq.mybatisplus.taste.plugins.pagination.IDialect;

/**
 * Postgre 数据库分页语句组装实现 ("limit 0 offset 15")
 */
public class PostgreDialect implements IDialect {
    @Override
    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuffer sql = new StringBuffer(originalSql);
        sql.append(" limit ").append(limit).append(" offset ").append(offset);
        return sql.toString();
    }
}
