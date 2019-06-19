package im.lincq.mybatisplus.taste.framework.service;

import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author lincq
 * @date 2019/6/16 22:57
 */
public interface ISuperService<T> {

    /**
     * 插入一条记录
     *
     * @param entity  实体对象
     * @return boolean
     */
    boolean insert(T entity);

    /**
     * 插入（批量），该方法不适合Oracle
     *
     * @param entityList 实体对象列表
     * @return  boolean
     */
    boolean insertBatch(List<T> entityList);

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return boolean
     */
    boolean deleteById( Long id );

    /**
     * 根据 entity 条件删除，删除记录
     * @param entity 实体对象
     * @return boolean
     */
    boolean deleteSelective(T entity);

    /**
     * 删除（根据ID批量删除）
     *
     * @param idList 主键ID集合
     * @return boolean
     */
    boolean deleteBatchIds(List<Long> idList);

    /**
     * 根据ID修改
     *
     * @param entity 实体对象
     * @return boolean
     */
    boolean updateById( T entity );

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return boolean
     */
    T selectById(Long id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表
     * @return boolean
     */
    List<T> selectBatchIds( List<Long> idList );

    /**
     * 根据entity条件，查询一条记录
     *
     * @param entity 数据实体
     * @return boolean
     */
    T selectOne(T entity);

    /**
     * 根据entity条件，查询全部记录
     *
     * @param rowBounds 分页查询条件（可以为RowBounds.DEFAULT，即不分页）
     * @param ew  实体对象封装类（可以为null）
     * @return boolean
     */
    List<T> selectList(RowBounds rowBounds, EntityWrapper<T> ew);

    /**
     * <p>查询列表</p>
     *
     * @param entity                实体对象
     * @param orderByField    对应 EntityWrapper 类中 orderByField 字段 {@link EntityWrapper }
     * @return                            数据结果集
     */
    List<T> selectList(T entity, String orderByField);

    List<T> selectList(T entity);

    /**
     * <p>翻页查询</p>
     *
     * @param page      翻页对象
     * @param entity     实体对象
     * @param orderByField       对应 EntityWrapper 类中 orderByField 字段
     * @return                数据结果集
     */
    Page<T> selectPage(Page<T>page, T entity, String orderByField);

    Page<T> selectPage(Page<T> page, T entity);
}
