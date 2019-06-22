package im.lincq.mybatisplus.taste.plugins.pagination;

import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;
import org.apache.ibatis.session.RowBounds;

/**
 * 简单分页模型
 * 用户可以通过继承org.apache.ibatis.session.RowBounds实现自己的分页模型
 * 注意，插件仅支持RowBounds及其子类作为分页参数
 */
public class Pagination extends RowBounds {
    /** 总记录条数 */
    private int total;
    /** 每页数量 */
    private int size;
    /** 总记录页数*/
    private int pages;
    /** 当前页 */
    private int current = 1;

    public Pagination () {
        super();
    }

    /**
     * 该构造函数会自动计算偏移量和界限值
     * 请注意：在知道总记录条数的情况下，插件是不会再查询总记录条数的
     * @param current  当前页
     * @param size         每页显示条数
     */
    public Pagination(int current, int size) {
        super(offsetCurrent(current ) * size, size);

        if (current > 1) {
            this.current = current;
        }
        this.size = size;

    }

    protected static int offsetCurrent(int current) {
        return (current > 0) ? current - 1 : 0;
    }

    public boolean hasPrevious() {
        return this.current > 1;
    }

    public boolean hasNext() {
        return this.current < this.pages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal (int total) {
        this.total = total;
        this.pages = this.total / this.size;
        if (this.total % this.size != 0) {
            this.pages++;
        }
        if (this.current > this.pages) {
            /* 当前页大于总页数，当前页设置为第一页 */
            this.current = 1;
        }
    }

    public int getSize() {
        return size;
    }

    public int getPages() {
        return pages;
    }

    public int getCurrent() {
        return current;
    }

    public int getCurrentOffset() {
        return this.current - 1;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "total=" + total +
                ", size=" + size +
                ", pages=" + pages +
                ", current=" + current +
                '}';
    }
}
