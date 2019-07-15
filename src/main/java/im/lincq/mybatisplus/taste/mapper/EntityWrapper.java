package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;

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
     *  SQL 片段
     */
    private String sqlSegment = null;

    /**
     * <p>SQL 排序 ORDER BY字段, 例如 id DESC（根据id降序查询）</p>
     * <p>DESC 表示降序排序（即：从大到小，从高到低）<br>ASC表示按照正序排序（即）</p>
     */
    private String orderByField = null;

    /**  基本保护EntityWrapper对象. */
    protected EntityWrapper () {}

    public EntityWrapper (T entity) {
        this.entity = entity;
    }

    public EntityWrapper (T entity, String orderByField) {
        this.entity = entity;
        this.orderByField = orderByField;
    }

    public EntityWrapper (T entity, String sqlSegment, String orderByField) {
        this.entity = entity;
        this.sqlSegment = sqlSegment;
        this.orderByField = orderByField;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity( T entity ) {
        this.entity = entity;
    }


    public String getSqlSegment () {
        if ( sqlSegment == null && orderByField == null) {
            return null;
        }
        StringBuffer andOrSql  = new StringBuffer();
        if ( sqlSegment != null ) {
            andOrSql.append(sqlSegment);
        }
        if ( orderByField != null ) {
            andOrSql.append(" ORDER BY  ").append(orderByField);
        }
        return stripSqlInjection(andOrSql.toString());
    }

    public void setSqlSegment( String sqlSegment ) {
        if ( sqlSegment != null && !"".equals(sqlSegment) ) {
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
     * SQL注入内容剥离
     * @param value 待处理内容
     * @return 处理结果
     */
    protected String stripSqlInjection(String value) {
        return value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
    }

}
