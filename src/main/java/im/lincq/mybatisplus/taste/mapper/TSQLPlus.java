package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.MybatisAbstractSQL;
import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.text.MessageFormat;

/**
 * @author lincq
 * @date 2019/8/15 14:27
 */
public class TSQLPlus extends MybatisAbstractSQL<TSQLPlus> {

    private final String IS_NOT_NULL = " IS NOT NULL";
    private final String IS_NULL = " IS NULL";

    @Override
    public TSQLPlus getSelf () {
        return this;
    }

    /**
     * 将LIKE语句添加到WHERE条件中
     * @param column 字段名
     * @param value      like值，无需前后%及ORACLE通用
     * @return this
     */
    public TSQLPlus LIKE (String column, String value) {
        handlerLike(column, value, false) ;
        return this;
    }

    /**
     * 将LIKE语句添加到WHERE条件中
     * @param column 字段名
     * @param value      like值，无需前后%及ORACLE通用
     * @return this
     */
    public TSQLPlus NOT_LIKE (String column, String value) {
        handlerLike(column, value, true) ;
        return this;
    }

    /**
     * IS NOT NULL查询
     * @param columns  以逗号分隔的字段名称
     * @return
     */
    public TSQLPlus IS_NOT_NULL (String columns) {
        handlerNull(columns, IS_NOT_NULL);
        return this;
    }

    /**
     * IS NULL查询
     * @param columns 以逗号分隔的字段名称
     * @return this
     */
    public TSQLPlus IS_NULL (String columns) {
        handlerNull(columns, IS_NULL);
        return this;
    }

    private void handlerLike (String column,   String value, boolean isNot) {
        if (StringUtils.isNotEmpty(column)) {
            String likeSql = " LIKE CONCAT(CONCAT({0}, {1}), {2})";
            if (isNot) {
                likeSql = " NOT" + likeSql;
            }
            String percent = StringUtils.quotaMark("%");
            WHERE(column + MessageFormat.format(likeSql, percent, StringUtils.quotaMark(value), percent));
        }
    }

    /**
     * 以相同的方式处理null和notnull
     * @param columns  以逗号分隔的字段名称
     * @param sqlPart     SQL部分
     */
    public void handlerNull (String columns, String sqlPart) {
        if (StringUtils.isNotEmpty(columns)) {
            String[] cols = columns.split(",");
            for (String col : cols) {
                if (StringUtils.isNotEmpty(col.trim()));
                WHERE(col + sqlPart);
            }
        }
    }
}
