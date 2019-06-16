package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lincq
 * @date 2019/6/16 23:09
 */
public class SuperServiceImpl<T> {

    @Autowired
    protected AutoMapper<T> autoMapper;

    public boolean retBool (int result) {
        return result >= 1;
    }

    public boolean insert(T entity) {
        return retBool(autoMapper.insert(entity));
    }

    public boolean insertBatch (List<T> entityList) {
        return retBool(autoMapper.insertBatch(entityList));
    }

    public boolean deleteById(Object id) {
        return retBool(autoMapper.deleteById(id));
    }

    public boolean deleteSelective(T entity) {
        return retBool(autoMapper.deleteSelective(entity));
    }

    public boolean deleteBatchIds(List<Object> idList) {
        return retBool(autoMapper.deleteBatchIds(idList));
    }

    public boolean updateById (T entity) {
        return retBool(autoMapper.updateById(entity));
    }

    public T selectById(Object id) {
        return autoMapper.selectById(id);
    }

    public List<T> selectBatchIds(List<Object> idList) {
        return autoMapper.selectBatchIds(idList);
    }

    public T selectOne(T entity) {
        return autoMapper.selectOne(entity);
    }

    public List<T> selectList(RowBounds rowBounds, EntityWrapper<T> entityWrapper) {
        return autoMapper.selectList(rowBounds, entityWrapper);
    }

}
