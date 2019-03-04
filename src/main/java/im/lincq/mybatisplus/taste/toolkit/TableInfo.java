package im.lincq.mybatisplus.taste.toolkit;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <p>
 * 字段反射辅助类
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
     * 表主键ID
     */
    private String tableId;
    /**
     * 表字段列表
     */
    private List<String> fieldList;

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

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public List<String> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }
}
