package im.lincq.mybatisplus.taste.plugins;

import im.lincq.mybatisplus.taste.plugins.pagination.Pagination;
import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author lincq
 * @date 2019/5/21 07:24
 */
public class Page<T> extends Pagination {

    /**
     * 当前页的数据列表
     */
    List<T> records = Collections.emptyList();

    /**
     * <p>SQL 排序ORDER BY字段，例如 id DESC （根据 id倒序查询）</p>
     * <p>
     *     DESC 表示按倒序排序（即：从大到小排序）<br>
     *     ASC 表示按正序排序（即：从小到大排序）<br>
     * </p>
     */
    private String orderByField;

    /**
     * 是否为升序ASC（默认：true）
     */
    private boolean isAsc = true;

    public Page() {
        /* 注意，传入翻页参数 */
    }

    public Page(int current, int size) {
        super(current, size);
    }

    public Page(int current, int size, String orderByField) {
        super(current, size);
        this.setOrderByField(orderByField);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
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

    public void setAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }

    @Override
    public String toString() {
        StringBuffer pg = new StringBuffer();
        pg.append(" Page:{ [").append(super.toString()).append("], ");
        if (records != null) {
            pg.append("records-size:").append(records.size());
        } else {
            pg.append("records is null");
        }
        return pg.append(" }").toString();
    }

}
