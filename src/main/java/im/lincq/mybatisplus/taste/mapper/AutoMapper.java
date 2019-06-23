package im.lincq.mybatisplus.taste.mapper;

import org.apache.ibatis.annotations.Param;
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
     * <p>
     * 插入一条记录
     * </p>
     * @param entity 对象实体
     * @return             int
     * */
    int insert(T entity);

    /**
     * <p>插入一条记录（选择字段， null 字段不插入）</p>
     *
     * @param entity 实体对象
     * @return int
     */
    int insertSelective(T entity);

    /**
     * <p>
     *  插入（批量），该方法不适合Oracle
     * </p>
     * @param entityList 实体对象列表
     * @return                   int
     */
    int insertBatch(List<T> entityList) ;

    /**
     * <p>
     * 根据ID删除
     * </p>
     * @param id 主键ID
     * @return       int
     * */
    int deleteById(Long id);

    /**
     * <p>
     * 根据 entity 条件,删除记录
     * </p>
     * @param entity 实体对象
     * @return int
     */
    int deleteSelective(@Param("ew") T entity);

    /**
     * <p>
     * 根据ID修改
     * </p>
     * @param entity 实体对象
     * @return int
     * */
    public int updateById(@Param("et") T entity);

    /**
     * <p>根据 ID 选择修改</p>
     *
     * @param entity 实体对象
     * @return int
     */
    int updateSelectiveById(@Param("et") T entity);

    /**
     * <p>根据 whereEntity 条件，更新记录</p>
     *
     * @param entity 实体对象（实际修改部分）
     * @param whereEntity 实体查询条件
     * @return
     */
    int update(@Param("et")T entity, @Param("ew") T whereEntity);

    /**
     * <p>根据 whereEntity 条件，选择更新记录</p>
     *
     * @param entity  实体对象
     * @param whereEntity 实体查询条件
     * @return int
     */
    int updateSelective( @Param("et" ) T entity, @Param("ew") T whereEntity);

    /**
     * <p>
     * 删除（批量）
     * </p>
     * @param idList 主键ID列表
     * @return List<T>
     */
    int deleteBatchIds(List<Long> idList);


    /**
     * <p>
     * 查询（批量）
     * </p>
     * @param idList 主键ID列表
     * @return List<T>
     */
    List<T> selectBatchIds(List<Long> idList);

    /**
     * <p>
     * 根据ID查询
     * </p>
     * @param id 主键ID
     * @return List<T>
     * */
    public T selectById(Long id);


    /**
     * <p>
     * 根据entity查询一条记录
     * </p>
     * @param entity 对象实体
     * @return T
     */
    T selectOne(@Param("ew") T entity);



    /**
     * <p>
     * 根据 entity 分页，查询全部记录
     * </p>
     * @param rowBounds       分页查询条件（可以为 RowBounds.DEFAULT）
     * @param  entityWrapper 实体对象封装操作类（可以为 null）
     * @return List<T>
     * */
    List<T> selectList(RowBounds rowBounds, @Param("ew")EntityWrapper<T> entityWrapper);

}
