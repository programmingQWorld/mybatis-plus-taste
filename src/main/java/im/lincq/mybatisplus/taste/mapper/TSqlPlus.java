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
            String inSql = " EXISTS ( %s )";
            if (isNot) {
                inSql = " NOT" + inSql;
            }
            WHERE(String.format(inSql, value));
        }
    }

    /**
     * 处理IN 操作
     * @param column  字段名称
     * @param value   集合List
     * @param isNot   是否为NOT IN操作
     */
    private void handlerIn (String column, List value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && CollectionUtil.isNotEmpty(value)) {
            String inSql = " IN ( %s )";
            if (isNot) {
                inSql = " NOT" + inSql;
            }
            StringBuilder inValue = new StringBuilder();
            for (int i = 0; i < value.size(); i++) {
                Object tempVal = value.get(i);
                // 如果tempVal 是 String, 且为拼接 单引号
                if (tempVal instanceof String && !String.valueOf(tempVal).matches("\'(.+)\'")) {
                    tempVal = StringUtils.quotaMark(String.valueOf(tempVal));
                }
                if (i + 1 == value.size()) {
                    inValue.append(tempVal);
                } else {
                    inValue.append(tempVal);
                    inValue.append(",");
                }
            }
            WHERE(column + String.format(inSql, inValue.toString()));
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
            String inSql = " IN (%s)";
            if (isNot) {
                inSql = " NOT" + inSql;
            }
            WHERE(column + String.format(inSql, value));
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
