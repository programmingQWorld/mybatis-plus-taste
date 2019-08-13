package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;
import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.text.MessageFormat;

/**
 * <p>Entity封装操作类</p>
 *
 * @author lincq
 * @date 2019/6/16 14:19
 */
public class EntityWrapper<T> {

    /**
     * 数据库表映射实体类
     */
    private T entity = null;

    /**
     * SQL 查询字段内容，例如：id,name,age
     */
    private String sqlSelect = null;

    /**
     *  SQL 片段
     */
    private String sqlSegment = null;

    public String getSqlSelect() {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            return sqlSelect;
        }
        return stripSqlInjection(sqlSegment);
    }

    public void setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    /**
     * <p>SQL 排序 ORDER BY字段, 例如 id DESC（根据id降序查询）</p>
     * <p>DESC 表示降序排序（即：从大到小，从高到低）<br>ASC表示按照正序排序（即）</p>
     */
    private String orderByField = null;

    /**
     * 查询条件
     */
    protected StringBuffer queryFilter = new StringBuffer();

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

        /* ===只排序 、直接返回=== */
        if (tempQuery.toUpperCase().indexOf("ORDER BY") == 0) {
            return stripSqlInjection(tempQuery.toString());
        }

        /* 条件部分不为空，对条件SQL做拼接 */
        StringBuffer sqlSegment = new StringBuffer();
        if (null == this.getEntity()) {
            sqlSegment.append(" WHERE ");
        } else {
            sqlSegment.append(" AND ");
        }

        sqlSegment.append(queryFilter);
        return stripSqlInjection(sqlSegment.toString());
    }

    public void setSqlSegment( String sqlSegment ) {
        if ( StringUtils.isNotEmpty(sqlSegment) ) {
            this.sqlSegment = sqlSegment;
        }
    }

    public String getOrderByField() {
        return this.orderByField;
    }

    public void setOrderByField () {
        if ( orderByField != null && !"".equals(orderByField) ) {
            this.orderByField = orderByField;
        }
    }

    /**
     * <p> 添加查询条件 </p>
     * <p>
     *     例如： ew.addFilter("name = {0}", "'123'")<br>
     *     输出：name = '123'<br>
         * </p>
     * @param filter       sql片段内容，如例子中的 "name={0}"
     * @param params  格式参数，组装sql的参数
     */
    public EntityWrapper<T> addFilter (String filter, Object ... params) {
        if (StringUtils.isEmpty(filter)) {
            return this;
        }
        if (null != params && params.length >= 1) {
            queryFilter.append(MessageFormat.format(filter, params));
        } else {
                queryFilter.append(filter);
        }

        return this;
    }

    /**
     * <p>添加查询条件</p>
     * <p>
     *     例如：ew.addFilter("name = {0}", "'123'").addFilterIfNeed(false, " ORDER BY id") <br>
     *     输出:  name = '123'
     * </p>
     * @param willAppend 判断条件 true 输出 SQL 片段，false 不输出
     * @param filter            SQL 片段
     * @param params       格式参数
     * @return EntityWrapper
     */
    public EntityWrapper<T> addFilterIfNeed (boolean willAppend, String filter, Object ... params) {
        if (willAppend) {
            addFilter(filter, params);
        }
        return this;
    }


    /**
     * SQL注入内容剥离
     * @param value 待处理内容
     * @return 处理结果
     */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }

}
