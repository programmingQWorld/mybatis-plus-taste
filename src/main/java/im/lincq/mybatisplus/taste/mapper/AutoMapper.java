package im.lincq.mybatisplus.taste.mapper;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p>
 *     Mapper继承该接口后，无需编写mapper.xml 文件.,即可获取CRUD功能.
 * </p>
 * @Date 20190121
 */
public interface AutoMapper<T> {
    /**
     * 插入
     * */
    public int insert(T entity);

    /**
     * 根据ID删除
     * */
    public int deleteById(Object id);

    /**
     * 根据 entity 条件删除记录
     */
    public int deleteSelective(T entity);

    /**
     * 根据ID修改
     * */
    public int updateById(T entity);

    /**
     * 删除（批量）
     */
    int deleteBatchIds(List idList);


    /**
     * 查询（批量）
     */
    List<T> selectBatchIds(List idList);

    /**
     * 根据ID查询
     * */
    public T selectById(Object id);


    /**
     * 根据entity查询一条记录
     */
    T selectOne(T entity);

    /**
     * 根据 entity 分页查询全部记录
     * @param rowBounds  分页查询条件（可以为 null）
     * @param  entity           实体对象（可以为 null）
     * */
    List<T > selectList (RowBounds rowBounds, T entity);
}
