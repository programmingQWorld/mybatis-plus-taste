package im.lincq.mybatisplus.taste.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 此类处理sql ResultSet 与 实体类之间的转换.
 * 具体转换处理，请阅读BeanProcessor.
 * @author cover by lin-cq
 */
public class BeanHandler<T> implements ResultSetHandler<T> {

    private final Class<T> type;
    private BeanProcessor converter;
    public BeanHandler(Class<T> type) {
        this.type = type;
        this.converter = new BeanProcessor();
    }


    /**
     * 功能：convert ResultSet to Bean object （处理数量是一）
     * @param rs    ResultSet
     * @return      泛型实体类
     */
    @Override
    public T handler (ResultSet rs) {
        try {
            return rs.next() ? this.converter.toBean(rs, this.type): null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
