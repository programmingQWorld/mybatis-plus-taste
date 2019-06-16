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
     * <p>SQL 排序 ORDER BY字段, 例如 id DESC（根据id降序查询）</p>
     * <p>DESC 表示降序排序（即：从大到小，从高到低）<br>ASC表示按照正序排序（即）</p>
     */
    private String orderByField = null;

    /**  基本保护EntityWrapper对象. */
    protected EntityWrapper () {

    }

    public EntityWrapper (T entity, String orderByField) {
        this.entity = entity;
        this.orderByField = orderByField;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity( T entity ) {
        this.entity = entity;
    }

    public String getOrderByField() {
        return this.orderByField;
    }

    public void setOrderByField () {
        if ( this.orderByField != null ) {
            /* 判断是否存在SQL注入*/
            String ob = orderByField.toUpperCase();
            if (ob.contains("INSERT") || ob.contains("DELETE") || ob.contains("UPDATE") || ob.contains("SELECT")) {
                throw new MybatisPlusException(" orderBy=["+ orderByField +"], There may be SQL injection");
            } else {
                this.orderByField = orderByField;
            }
        }
    }


}
