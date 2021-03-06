package im.lincq.mybatisplus.taste.plugins.pagination.dialects;

import im.lincq.mybatisplus.taste.plugins.pagination.IDialect;

/**
 * HSQL 数据库分页语句组装实现 ("limit 0, 10")
 */
public class HSQLDialect implements IDialect  {
    @Override
    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder(originalSql);
        sql.append(" limit ").append(offset).append(",").append(limit);
        return sql.toString();
    }
}
