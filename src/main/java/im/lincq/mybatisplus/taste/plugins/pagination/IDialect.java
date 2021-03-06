package im.lincq.mybatisplus.taste.plugins.pagination;

/**
 * 数据库 分页组装语句接口
 * hubin
 * copy by
 * lincq -2019-03-07
 */
public interface IDialect {
    /**
     * 组装分页语句
     * @param originalSql   分页前sql
     * @param offset            偏移量
     * @param limit               界限，获取数据行数
     * @return                         分页语句
     */
    String buildPaginationSql(String originalSql, int offset, int limit);
}
