package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;
import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.text.MessageFormat;

/**
 * <p>Entity封装操作类，定义T-SQL语法</p>
 *
 * @author hubin, yanghu, DYang
 * @date 2019/6/16 14:19
 */
public class EntityWrapper<T> extends QueryFilter {

    /**
    * WHERE关键字
    */
    private final String WHERE = " WHERE ";
    /**
     * AND关键字
     */
    private final String AND = " AND ";
    /**
     * OR关键字
     */
    private final String OR = " OR ";
    /**
     * GROUP BY关键字
     */
    private final String GROUPBY = " GROUP BY ";
    /**
     * HAVING关键字
     */
    private final String HAVING = " HAVING ";
    /**
     * ORDER BY关键字
     */
    private final String ORDERBY = " ORDER BY ";
    /**
     * DESC关键字
     */
    private final String DESC = " DESC ";
    /**
     * ASC关键字
     */
    private final String ASC = " ASC ";

    /**
     * 是否使用了T-SQL语法
     */
    protected boolean tsql = false;

    /**
     * 数据库表映射实体类
     */
    private T entity = null;

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private String sqlSelect = null;

    /**
     * 查询条件
     */
    protected StringBuffer queryFilter = new StringBuffer();

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

    public EntityWrapper () {
        // to do nothing
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
        String tempQuery = queryFilter.toString().trim();
        if (StringUtils.isEmpty(tempQuery)) {
            return null;
        }
        /** SQL片段，兼容非T-SQL语法*/
        if (!tsql) {
            StringBuffer sqlSegment = new StringBuffer();
            if (null == this.getEntity()) {
                sqlSegment.append(WHERE);
            } else {
                sqlSegment.append(AND);
            }
            sqlSegment.append(queryFilter.toString());
            return stripSqlInjection(sqlSegment.toString());
        }
        // 使用防SQL注入处理后返回
        return stripSqlInjection(queryFilter.toString());
    }

    /**
     * <p>SQL中WHERE关键字跟的条件语句</p>
     * ,<p>eg: ew.where("name='zhangsan'").and("id={0}", 22).and("password is not null")</p>
     * @param sqlWhere where语句
     * @param params    参数集
     * @return this
     */
    public EntityWrapper<T> where (String sqlWhere, Object ... params) {
        if (tsql) {
            throw new MybatisPlusException("SQL already contains the String where.");
        }
        /** 使用T-SQL语法 */
        tsql = true;
        if (null == this.getEntity()) {
            addFilter(WHERE, sqlWhere, params);
        } else {
            addFilter(AND, sqlWhere, params);
        }
        return this;
    }

    /**
     * <p>SQL中AND关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").and("id={0}", 22).and("password is not null")</p>
     * @param sqlAnd and 连接串
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> and (String sqlAnd, Object ... params) {
        addFilter(AND, sqlAnd, params);
        return this;
    }

    /**
     * <p>与and方法的区别是，可根据需要判断是否添加该条件</p>
     * @param need      是否需要使用该and条件
     * @param sqlAnd   and条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> andIfNeed(boolean need, String sqlAnd, Object ... params) {
        addFilterIfNeed(need, AND, sqlAnd, params);
        return this;
    }

    /**
     * <p>SQL中OR关键字跟的条件语句</p>
     * @param sqlOr     or条件语句
     * @param params 参数集
     * @return this
     */
    public EntityWrapper<T> or (String sqlOr, Object ... params) {
        addFilter(OR, sqlOr, params);
        return this;
    }

    /**
     * <p>与or方法的区别是 可根据需要判断是否添加该条件</p>
     * @param need          是否需要使用OR条件
     * @param sqlOr         OR条件语句
     * @param params     参数集
     * @return this
     */
    public EntityWrapper<T> orIfNeed(boolean need, String sqlOr, Object ... params) {
        addFilterIfNeed(need, OR, sqlOr, params);
        return this;
    }

    /**
     * <p> SQL中 groupBy 关键字跟的条件语句</p>
     * <p>eg: ew.where("name='zhangsan'").and("id={0}", 22).and("password is not null").groupBy("id, name")</p>
     * @param sqlGroupBy sql中的Group by语句，无需输入Group By关键字
     * @return this
     */
    public EntityWrapper<T> groupBy (String sqlGroupBy) {
        addFilter(GROUPBY, sqlGroupBy);
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
        addFilter(HAVING, sqlHaving, params);
        return this;
    }

    /**
     * <p>SQL中order by 关键字跟的条件语句</p>
     * <p>
     *     eg: ew.groupBy("id,name").having("id={0}",22).and("password is not null").orderBy("id, name")
     * </p>
     * @param sqlOrderBy  sql中的order b语句，无需输入Order By关键字
     * @return this
     */
    public EntityWrapper<T> orderBy(String sqlOrderBy) {
        addFilter(ORDERBY, sqlOrderBy);
        return this;
    }

    public EntityWrapper<T> orderBy(String sqlOrderBy, boolean isAsc) {
        addFilter(ORDERBY, sqlOrderBy);
        if (isAsc) {
            queryFilter.append(ASC);
        } else {
            queryFilter.append(DESC);
        }
        return this;
    }

    /**
     * <p>SQL注入内容剥离</p>
     * @param value  待处理内容
     * @return string
     */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }

}
