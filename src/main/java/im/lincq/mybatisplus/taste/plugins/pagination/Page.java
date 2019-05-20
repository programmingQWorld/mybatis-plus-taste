package im.lincq.mybatisplus.taste.plugins.pagination;

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

    protected Page() {
        /* 保护 */
    }

    public Page(int current, int size) {
        super(current, size);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        StringBuffer pg = new StringBuffer();
        pg.append(" Page:{ [").append(super.toString()).append("], ");
        if ( records != null ) {
            pg.append("records-size:").append(records.size());
        } else {
            pg.append("records is null");
        }
        return pg.append(" }").toString();
    }

}
