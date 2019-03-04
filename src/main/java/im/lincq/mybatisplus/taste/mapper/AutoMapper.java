package im.lincq.mybatisplus.taste.mapper;

import java.util.List;

/**
 * <p>
 *     Mapper继承该接口后，无需编写mapper.xml 文件.,即可获取CRUD功能.
 * </p>
 * @Date 20190121
 */
public interface AutoMapper<T> {
    /**
     * 新增
     * */
    public int insert(T entity);

    /**
     * 根据主键删除，主键名称默认为id
     * */
    public int deleteById(Object id);

    /**
     * 根据主键修改，主键名称默认为id
     * */
    public int updateById(T entity);

    /**
     * 根据主键查找，主键名称默认为id
     * */
    public T selectById(Object id);

    /**
     * 查询全部
     * */
    public List<T > selectAll ();
}
