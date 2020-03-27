package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.MybatisAbstractSQL;
import im.lincq.mybatisplus.taste.toolkit.CollectionUtil;
import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author lincq
 * @date 2019/8/15 14:27
 */
@SuppressWarnings("serial")
public class TSqlPlus extends MybatisAbstractSQL<TSqlPlus> {

    private final String IS_NOT_NULL = " IS NOT NULL";
    private final String IS_NULL = " IS NULL";
    private final String SQL_LIKE = " LIKE CONCAT({0}, {1}, {2})";
    private final String SQL_BETWEEN_AND = " BETWEEN {0} AND {1}";

    @Override
    public TSqlPlus getSelf () {
        return this;
    }

    /**
     * 将LIKE语句添加到WHERE条件中
     * @param column 字段名
     * @param value      like值，无需前后%及ORACLE通用
     * @return this
     */
    public TSqlPlus LIKE (String column, String value) {
        handlerLike(column, value, false) ;
        return this;
    }

    /**
     * 将LIKE语句添加到WHERE条件中
     * @param column 字段名
     * @param value      like值，无需前后%及ORACLE通用
     * @return this
     */
    public TSqlPlus NOT_LIKE (String column, String value) {
        handlerLike(column, value, true) ;
        return this;
    }

    /**
     * IS NOT NULL查询
     * @param columns  以逗号分隔的字段名称
     * @return
     */
    public TSqlPlus IS_NOT_NULL (String columns) {
        handlerNull(columns, IS_NOT_NULL);
        return this;
    }

    /**
     * IS NULL查询
     * @param columns 以逗号分隔的字段名称
     * @return this
     */
    public TSqlPlus IS_NULL (String columns) {
        handlerNull(columns, IS_NULL);
        return this;
    }

    private void handlerLike (String column,  String value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(column);
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(MessageFormat.format(SQL_LIKE, "'%'", StringUtils.quotaMark(value), "'%'"));
            WHERE(inSql.toString());
        }
    }

    /**
     * 将IN语句添加到WHERE条件中
     * @param column  column
     * @param value   值
     * @return        this
     */
    public TSqlPlus IN (String column, List value) {
        handlerIn(column, value, false);
        return this;
    }

    /**
     * 将NOT IN语句添加到WHERE条件中
     * @param column   字段名
     * @param value    List集合
     * @return  this
     */
    public TSqlPlus NOT_IN (String column, List value) {
        handlerIn(column, value, true);
        return this;
    }

    /**
     * 将IN语句添加到WHERE条件中
     *
     * @param column 字段名
     * @param value  逗号拼接的字符串
     * @return  this
     */
    public TSqlPlus IN(String column, String value) {
        handlerIn(column, value, false);
        return this;
    }

    /**
     * 将NOT_IN语句添加到WHERE条件中
     *
     * @param column 字段名
     * @param value  逗号拼接的字符串
     * @return  this
     */
    public TSqlPlus NOT_IN(String column, String value) {
        handlerIn(column, value, true);
        return this;
    }

    /**
     * 将EXISTS语句添加到WHERE条件中
     *
     * @param value  值
     * @return  this
     */
    public TSqlPlus EXISTS(String value) {
        handlerExists(value, false);
        return this;
    }

    /**
     * 将NOT_EXISTS语句添加到WHERE条件中
     *
     * @param value  值
     * @return  this
     */
    public TSqlPlus NOT_EXISTS(String value) {
        handlerExists(value, true);
        return this;
    }

    /**
     * 处理EXISTS操作
     * @param value 值
     * @param isNot 是否为NOT EXISTS操作
     */
    private void handlerExists (String value, boolean isNot) {
        if (StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" EXISTS (").append(value).append(")");
            WHERE(inSql.toString());
        }
    }

    /**
     * 处理IN 操作
     * @param column  字段名称
     * @param value   集合List
     * @param isNot   是否为NOT IN操作
     */
    private void handlerIn (String column, List<?> value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && CollectionUtil.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(column);
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" IN(");
            int _size = value.size();
            for (int i = 0; i < _size; i++) {
                Object tempVal = value.get(i);
                // 如果tempVal 是 String, 且为拼接 单引号
                if (tempVal instanceof String && !String.valueOf(tempVal).matches("\'(.+)\'")) {
                    tempVal = StringUtils.quotaMark(String.valueOf(tempVal));
                }
                if (i + 1 == _size) {
                    inSql.append(tempVal);
                } else {
                    inSql.append(tempVal);
                    inSql.append(",");
                }
            }
            inSql.append(")");
            WHERE(inSql.toString());
        }
    }

    /**
     * 处理IN 操作
     * @param column  字段名称
     * @param value   逗号拼接的字符串
     * @param isNot   是否为NOT IN操作
     */
    private void handlerIn (String column, String value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(column);
            if (isNot) {
                inSql.append(" NOT");
            }
            inSql.append(" IN (").append(value).append(")");
            WHERE(inSql.toString());
        }
    }

    public TSqlPlus BETWEEN_AND (String column, String val1, String val2) {
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(val1) && StringUtils.isNotEmpty(val2)) {
            StringBuilder betweenSql = new StringBuilder();
            betweenSql.append(column);
            betweenSql.append(MessageFormat.format(SQL_BETWEEN_AND, val1, val2));
            WHERE(betweenSql.toString());
        }
        return this;
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
