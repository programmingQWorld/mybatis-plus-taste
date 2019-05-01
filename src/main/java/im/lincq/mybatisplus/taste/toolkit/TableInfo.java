package im.lincq.mybatisplus.taste.toolkit;

import im.lincq.mybatisplus.taste.toolkit.TableFieldInfo;
import java.util.List;
/**
 * <p>
 * 数据库表反射信息
 * </p>
 */
public class TableInfo {
    /**
     * 表主键ID是否自增
     */
    private boolean autoIncrement;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表主键ID 属性名
     */
    private String keyProperty;
    /**
     * 表主键ID 字段名
     */
    private String keyColumn;
    /**
     * 表非主键字段列表(TableFieldInfo可以从字段属性为映射)
     */
    private List<TableFieldInfo> fieldList;

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public List<TableFieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<TableFieldInfo> fieldList) {
        this.fieldList = fieldList;
    }
}
