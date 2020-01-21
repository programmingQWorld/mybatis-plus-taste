package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * <p>Entity封装操作类，定义T-SQL语法</p>
 *
 * @author hubin, yanghu, DYang
 * @date 2019/6/16 14:19
 */
@SuppressWarnings("serial")
public class EntityWrapper<T> implements Serializable {

    /**
     * 数据库表映射实体类
     */
    private T entity = null;

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private String sqlSelect = null;

    /**
     * 实现了TSQL语法的SQL实体
     */
    protected TSqlPlus sql = new TSqlPlus();

    /**
     * 查询条件
     */
    protected StringBuffer queryFilter = new StringBuffer();

    public EntityWrapper () {
        /* 注意，传入查询参数 */
    }

    public EntityWrapper (T entity) {
        this.entity = entity;
    }


    public EntityWrapper (T entity, String sqlSelect) {
        this.entity = entity;
        this.sqlSelect = sqlSelect;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity( T entity ) {
        this.entity = entity;
    }

    /**
     * SQL片段 ( where 后面t条件sql部分 )
     */
    public String getSqlSegment () {
        /* ===无条件SQL片段, 在这里忽略了queryFilter的前后空格 === */
        String sqlWhere = sql.toString().trim();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        }

        // 根据当前实体判断是哦夫需要将WHERE 替换成 AND
        sqlWhere = (null != entity) ? sqlWhere.replaceFirst("WHERE", "AND") : sqlWhere;

        // 使用防SQL注入处理后返回
        return stripSqlInjection(sqlWhere);
    }

    /**
     * <p>SQL中WHERE关键字跟的条件语句</p>
     * ,<p>eg: ew.where("name='zhangsan'").where("id={0}", 123)</p>
     * <p>输出：WHERE (NAME='zhangsan' AND id=123)</p>
     * @param sqlWhere where语句
     * @param params    参数集
     * @return this
     */
    public EntityWrapper<T> where (String sqlWhere, Object ... params) {
        sql.WHERE(formatSql(sqlWhere, params));
        return this;
    }

    /**
     * <p>AND 连接后续条件</p>
     * @param sqlAnd and and 条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> and (String sqlAnd, Object ... params) {
        sql.AND().WHERE(formatSql(sqlAnd, params));
        return this;
    }



    /**
     * <p>使用AND连接并换行</p>
     * <p> eg: ew.where("name='zhangsan'").and("id=11").andNew("status=1")</p>
     * <p>输出：
     * WHERE (name='zhangsan' AND id=11)<br>
     *  AND (status=1)
     * </p>
     * @param sqlAnd   and 条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> andNew(String sqlAnd, Object ... params) {
        sql.AND_NEW().WHERE(formatSql (sqlAnd, params));
        return this;
    }

    /**
     * <p>添加OR条件</p>
     * @param sqlOr     or条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> or (String sqlOr, Object ... params) {
        sql.OR().WHERE(formatSql(sqlOr, params));
        return this;
    }

    /**
     * <p>与or方法的区别是 可根据需要判断是否添加该条件</p>
     * @param sqlOr         OR条件语句
     * @param params     参数集
     * @return this
     */
    public EntityWrapper<T> orNew(String sqlOr, Object ... params) {
        sql.OR_NEW().WHERE(formatSql (sqlOr, params));
        return this;
    }



    /**
     * <p> SQL中 groupBy 关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").and("id={0}", 22).and("password is not null").groupBy("id, name")</p>
     * @param columns sql中的Group by语句，无需输入Group By关键字
     * @return this
     */
    public EntityWrapper<T> groupBy (String columns) {
        sql.GROUP_BY(columns);
        return this;
    }



    /**
     * <p>SQL中 having 关键字跟的条件语句</p>
     * <p>eg: groupBy("id, name").having("id={0}", 22).and("password is not null")</p>
     * @param sqlHaving having关键字后面跟随的语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> having(String sqlHaving, Object ... params) {
        sql.HAVING(formatSql(sqlHaving, params));
        return this;
    }

    /**
     * <p>
     * SQL中orderby关键字跟的条件语句
     * </p>
     * <p>
     * eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null"
     * ).orderBy("id,name")
     * </p>
     *
     * @param columns SQL 中的 order by 语句，无需输入 Order By 关键字
     * @return this
     */
    public EntityWrapper<T> orderBy(String columns) {
        sql.ORDER_BY(columns);
        return this;
    }

    /**
     * <p>SQL中order by 关键字跟的条件语句</p>
     * <p>
     *     eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null").orderBy("id, name")
     * </p>
     * @param columns  sql中的order by语句，无需输入Order By关键字
     * @return this
     */
    public EntityWrapper<T> orderBy(String columns, boolean isAsc) {
        if (StringUtils.isNotEmpty(columns)) {
            sql.ORDER_BY(columns + (isAsc ? " ASC" : " DESC"));
        }
        return this;
    }

    public String getSqlSelect() {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            return sqlSelect;
        }
        return stripSqlInjection(sqlSelect);
    }

    public void setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
    }

    /**
     * LIKE条件语句，value中无需前后% 目前适配mysql及oracle
     * @param column 字段名称
     * @param value    匹配值
     * @return this
     */
    public EntityWrapper<T> like(String column, String value) {
        sql.LIKE(column, value);
        return this;
    }

    /**
     * NOT LIKE条件语句， value中无需前后%目前适配mysql及oracle
     * @param column  字段名称
     * @param value     匹配值
     * @return this
     */
    public EntityWrapper<T> notLike(String column, String value) {
       sql.NOT_LIKE(column, value);
       return this;
    }

    /**
     * is not null 条件
     *
     * @param columns 字段名称，多个字段以逗号分隔。
     * @return this
     */
    public EntityWrapper<T> isNotNull(String columns) {
        sql.IS_NOT_NULL(columns);
        return this;
    }

    /**
     * is null 条件
     * @param columns 字段名称，多个字段以逗号分隔
     * @return this
     */
    public EntityWrapper<T> isNull (String columns) {
        sql.IS_NULL(columns);
        return this;
    }

    /**
     * 为了兼容之前的版本，可使用where()或and()替代
     * @param sqlWhere  where sql 部分
     * @param params     参数集
     * @return this
     */
    public EntityWrapper<T> addFilter(String sqlWhere, Object ... params) {
        return and(sqlWhere, params);
    }

    /**
     * <p>
     * 根据判断条件来添加条件语句部分     使用 andIf() 替代
     * </p>
     * <p>
     * eg: ew.filterIfNeed(false,"name='zhangsan'").where("name='zhangsan'")
     * .filterIfNeed(true,"id={0}",22)
     * <p>
     * 输出: WHERE (name='zhangsan' AND id=22)
     * </p>
     *
     * @param need     是否需要添加该条件
     * @param sqlWhere 条件语句
     * @param params   参数集
     * @return this
     */
    public EntityWrapper<T> addFilterIfNeed(boolean need, String sqlWhere, Object... params) {
        return need ? where(sqlWhere, params) : this;
    }

        /**
         * <p>SQL注入内容剥离</p>
         * @param value  待处理内容
         * @return string
         */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }


    /**
     * formatSql的结果如果返回NULL，就会产生WHERE(NULL)这样的结果
     * 所以此处要判断后，根据结果来添加当前关键字处理
     * @param sqlPart 当前SQL语句
     * @return boolean
     */
    private boolean retNeed(String sqlPart) {
        return StringUtils.isNotEmpty(sqlPart);
    }

    /**
     * <p>
     *     格式化SQL
     * </p>
     *
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return return
     */
    protected String formatSql (String sqlStr, Object ... params) {
        return formatSqlIfNeed(true, sqlStr, params);
    }

    /**
     * <p>
     * 根据需要格式化SQL
     * </p>
     *
     * @param need   是否需要格式化
     * @param sqlStr SQL语句部分
     * @param params 参数集
     * @return this
     */
    protected String formatSqlIfNeed(boolean need, String sqlStr, Object ... params) {
        if (!need || StringUtils.isEmpty(sqlStr)) {
            return null;
        }
        if (null != params && params.length > 0) {
            dealParams(params);
            sqlStr = MessageFormat.format(sqlStr, params);
        }
        return sqlStr;
    }


    /**
     * <p>
     *     处理Object类型的参数，
     *     如果类型为String,自动添加单引号 'value' ，当前字符串已经包含单引号，则不做修改
     *     如果类型为Object，自动转换成String类型。
     * </p>
     *
     * @param params 参数集
     */
    protected void dealParams (Object[] params) {
        for (int i = 0; i < params.length; i++) {
            Object tempVal = params[i];
            if (tempVal instanceof String && !String.valueOf(tempVal).matches("\'(.+)\'")) {
                params[i] = StringUtils.quotaMark(String.valueOf(tempVal));
            } else {
                params[i] = StringUtils.getString(tempVal);
            }
        }
    }
}
