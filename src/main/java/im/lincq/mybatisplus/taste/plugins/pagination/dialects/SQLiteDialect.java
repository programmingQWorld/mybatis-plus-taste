package im.lincq.mybatisplus.taste.plugins.pagination.dialects;

import im.lincq.mybatisplus.taste.plugins.pagination.IDialect;

/**
 * SQLite 数据库分页语句组装实现("limit limit offset")
 */
public class SQLiteDialect implements IDialect {

    @Override
    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuffer sql = new StringBuffer(originalSql);
        sql.append(" limit ").append(limit).append(" offset ").append(offset);
        return sql.toString();
    }
}
