package im.lincq.mybatisplus.taste.toolkit;

/**
 * 数据库表字段反射信息
 */
public class TableFieldInfo {
    /**
     * 是否存在字段名和属性名关联. （是否自定义设置字段名）true, false
     */
    private boolean related;

    /**
     * 字段名
     */
    private String column;

    /**
     * 属性名
     */
    private String property;

    /**
     * 属性表达式#{property}, 可以指定jdbcType, typeHandler等
     */
    private String el;


    public TableFieldInfo(boolean related, String column, String property, String el) {
        this.related = related;
        this.column = DBKeywordsProcessor.convert(column);
        this.property = property;
        this.el = el;
    }

    public TableFieldInfo(boolean related, String column, String property) {
        this.related = related;
        this.column = DBKeywordsProcessor.convert(column);
        this.property = property;
        this.el = property;
    }

    public TableFieldInfo(String column) {
        this.related = false;
        this.property = column;
        this.column = DBKeywordsProcessor.convert(column);
        this.el = column;
    }

    public boolean isRelated() {
        return related;
    }

    public void setRelated(boolean related) {
        this.related = related;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getEl() {
        return el;
    }

    public void setEl(String el) {
        this.el = el;
    }
}
