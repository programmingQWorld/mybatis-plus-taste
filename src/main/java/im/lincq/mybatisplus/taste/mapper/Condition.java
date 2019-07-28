package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.toolkit.StringUtils;

/**
 * 查询条件
 *
 * @author lincq
 * @date 2019/7/28 12:03
 */
public class Condition<T> {

    /**
     * 根据实体类查询
     */
    private T whereEntity = null;

    /**
     * 需要查询的参数（可选字段）
     */
    private String sqlSelect = null;

    /**
     * 附加Sql片段，实现自定义查询
     */
    private String sqlSegment = null;

    /**
     * <p>SQL排序ORDER BY字段，例如：id DESC（根据 ID 倒叙查询）</p>
     * <p>
     *     DESC 表示按倒叙排序（即：从大到小排序）<br>
     *     ASC 表示按正序排序（即：从小到大排序）
     * </p>
     */
    private String orderByField = null;

    /**
     * 是否为升序ASC(默认：true)
     */
    private boolean isAsc = true;

    /**
     * 封装EntityWrapper
     */
    private EntityWrapper<T> entityWrapper = null;

    public T getWhereEntity() {
        return whereEntity;
    }

    public void setWhereEntity(T whereEntity) {
        this.whereEntity = whereEntity;
    }

    public String getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(String sqlSelect) {
        if (StringUtils.isNotEmpty(sqlSelect)) {
            this.sqlSelect = sqlSelect;
        }
    }

    public String getSqlSegment() {
        return sqlSegment;
    }

    public void setSqlSegment(String sqlSegment) {
        if (StringUtils.isNotEmpty(sqlSegment)) {
            this.sqlSegment = sqlSegment;
        }
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        if (StringUtils.isNotEmpty(orderByField)) {
            this.orderByField = orderByField;
        }
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean asc) {
        isAsc = asc;
    }

    public EntityWrapper<T> getEntityWrapper() {
        return new EntityWrapper<T>(whereEntity, sqlSelect, convertSqlSegment(sqlSegment, orderByField, isAsc));
    }
    public void setEntityWrapper(EntityWrapper<T> entityWrapper) {
        this.entityWrapper = entityWrapper;
    }

    private String convertSqlSegment(String sqlSegment, String orderByField, boolean isAsc) {
        StringBuffer segment = new StringBuffer();
        if (StringUtils.isNotEmpty(sqlSegment)) {
            segment.append(sqlSegment);
        } else {
            segment.append(" 1=1 ");
        }
        if (StringUtils.isNotEmpty(orderByField)) {
            segment.append(" ORDER BY ").append(orderByField);
            if (!isAsc) {
                segment.append(" DESC");
            }
        }
        return segment.toString();
    }
}
