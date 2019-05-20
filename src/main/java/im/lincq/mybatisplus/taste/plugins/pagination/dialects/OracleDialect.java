package im.lincq.mybatisplus.taste.plugins.pagination.dialects;

import im.lincq.mybatisplus.taste.plugins.pagination.IDialect;

/**
 * Oracle 数据库分页语句组装实现
 */
public class OracleDialect implements IDialect {

    @Override
    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder(originalSql);
        /*
         *  Oracle分页是通过rownumber进行的，rownumber是从1开始的,
         * 分页查找按照起始位置Number和结束位置Number获取区间内的行数据
         *  需要往外面补充两层循环.
         * */
        offset++;
        sql.insert(0, "SELECT U.*, ROWNUM R FROM(").append(") where rownum < ").append(offset + limit);
        sql.insert(0, "SELECT * FROM (").append(") TEMP WHERE R >= ").append(offset);
        return sql.toString();
    }
}