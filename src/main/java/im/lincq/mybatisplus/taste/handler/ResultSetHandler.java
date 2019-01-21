package im.lincq.mybatisplus.taste.handler;

import java.sql.ResultSet;

/**
 * @author cover by lin-cq
 * @param <T>
 */
public interface ResultSetHandler<T> {
    /**
     * //TODO 补全注释
     * @param rs
     * @return
     */
    T handler(ResultSet rs);
}
