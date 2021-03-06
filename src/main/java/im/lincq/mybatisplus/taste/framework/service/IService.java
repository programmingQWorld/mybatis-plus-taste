package im.lincq.mybatisplus.taste.framework.service;

import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author lincq
 * @date 2019/6/29 12:58
 */
public interface IService<T, PK> {


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
     * <p>
     *     TableId 主键存在则更新记录，否则插入一条记录.
     *     关键点在于主键是否有值
     * </p>
     * @param entity 实体对象
     * @return boolean
     */
    boolean insertOrUpdate (T entity);

    /**
     * <p>
     *     TableId 主键存在则更新记录，否则插入一条记录. （选择字段，值为null时该字段不插入）
     *     关键点在于主键是否有值
     * </p>
     * @param entity 实体对象
     * @return boolean
     */
    boolean insertOrUpdateSelective (T entity);

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return boolean
     */
    boolean deleteById( PK id );

    /**
     *<p>根据 columnMap 条件，删除记录</p>
     *
     * @param columnMap 表字段 map 对象
     * @return boolean
     */
    boolean deleteByMap(Map<String, Object> columnMap);

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
    boolean deleteBatchIds(List<PK> idList);

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
    T selectById(PK id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表
     * @return boolean
     */
    List<T> selectBatchIds( List<PK> idList );

    /**
     * <p>查询（根据 columnMap 条件）</p>
     *
     * @param columnMap 表字段 map 对象
     * @return List<T>
     */
    List<T> selectByMap (Map<String, Object> columnMap);

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
    int selectCount(T entity);


    /**
     * <p>查询列表</p>
     *

     * @return
     */

    /**
     * <p>查询列表</p>
     * @param entityWrapper 实体包装类，详见EntityWrapper类{@link EntityWrapper}
     * @return                          数据结果集
     */
    List<T> selectList(EntityWrapper<T> entityWrapper);

    /**
     * <p>翻页查询</p>
     *
     * @param page                  翻页对象
     * @param entityWrapper 实体包装类，详见EntityWrapper类{@link EntityWrapper}
     * @return                           数据结果集
     */
    Page<T> selectPage(Page<T>page, EntityWrapper<T> entityWrapper);

}
