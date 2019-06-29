package im.lincq.mybatisplus.taste.framework.service;

import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lincq
 * @date 2019/6/29 12:58
 */
public interface IService<T, I> {

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
     * <p>插入一条记录（选择字段， null 字段不插入）</p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    boolean insertSelective(T entity);

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return boolean
     */
    boolean deleteById( I id );

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
    boolean deleteBatchIds(List<I> idList);

    /**
     * <p>
     * 根据ID修改
     * </p>
     * @param entity 实体对象
     * @return boolean
     * */
    boolean updateById(@Param("et") T entity);

    /**
     * <p>根据 ID 选择修改</p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    boolean updateSelectiveById(@Param("et") T entity);

    /**
     * <p>根据 whereEntity 条件，更新记录</p>
     *
     * @param entity 实体对象（实际修改部分）
     * @param whereEntity 实体查询条件 （可以为空，为空时候，匹配到全部数据）
     * @return boolean
     */
    boolean update(@Param("et")T entity, @Param("ew") T whereEntity);

    /**
     * <p>根据 whereEntity 条件，选择更新记录</p>
     *
     * @param entity  实体对象
     * @param whereEntity 实体查询条件 （可以为空，为空时候，匹配到全部数据）
     * @return boolean
     */
    boolean updateSelective( @Param("et" ) T entity, @Param("ew") T whereEntity);

    /**
     * <p>根据ID批量修改</p>
     *
     * @param entityList 实体对象列表
     * @return boolean
     */
    boolean updateBatchById( List<T> entityList );

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return boolean
     */
    T selectById(I id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表
     * @return boolean
     */
    List<T> selectBatchIds( List<I> idList );

    /**
     * 根据entity条件，查询一条记录
     *
     * @param entity 数据实体
     * @return boolean
     */
    T selectOne(T entity);

    /**
     * <p>根据 entity 条件，查询总记录数</p>
     * @param entity 实体对象
     * @return int
     */
    int selectCount( @Param("ew" ) T entity);

    /**
     * <p>查询列表</p>
     *
     * @param entity          实体对象
     * @param orderByField    对应 EntityWrapper 类中 orderByField 字段 {@link EntityWrapper }
     * @return                数据结果集
     */
    List<T> selectList(T entity, String orderByField);

    List<T> selectList(T entity);

    /**
     * <p>翻页查询</p>
     *
     * @param page          翻页对象
     * @param entity        实体对象
     * @param orderByField  对应 EntityWrapper 类中 orderByField 字段
     * @return              数据结果集
     */
    Page<T> selectPage(Page<T>page, T entity, String orderByField);

    Page<T> selectPage(Page<T> page, T entity);

}
