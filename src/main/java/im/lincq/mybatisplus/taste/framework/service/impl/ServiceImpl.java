package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.framework.service.IService;
import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.mapper.BaseMapper;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lincq
 * @date 2019/6/29 13:05
 */
public class ServiceImpl<M extends BaseMapper<T, I>, T, I> implements IService<T, I> {

    @Autowired
    protected M autoMapper;

    public boolean retBool (int result) {
        return result >= 1;
    }

    @Override
    public boolean insert(T entity) {
        return retBool(autoMapper.insert(entity));
    }

    @Override
    public boolean insertBatch (List<T> entityList) {
        return retBool(autoMapper.insertBatch(entityList));
    }

    @Override
    public boolean insertSelective(T entity) {
        return retBool(autoMapper.insertSelective(entity));
    }

    @Override
    public boolean deleteById(I id) {
        return retBool(autoMapper.deleteById(id));
    }

    @Override
    public boolean deleteSelective(T entity) {
        return retBool(autoMapper.deleteSelective(entity));
    }

    @Override
    public boolean deleteBatchIds(List<I> idList) {
        return retBool(autoMapper.deleteBatchIds(idList));
    }

    @Override
    public boolean updateById (T entity) {
        return retBool(autoMapper.updateById(entity));
    }

    @Override
    public boolean updateSelectiveById(T entity) {
        return retBool(autoMapper.updateSelectiveById(entity));
    }

    @Override
    public boolean update(T entity, T whereEntity) {
        return retBool(autoMapper.update(entity, whereEntity));
    }

    @Override
    public boolean updateSelective(T entity, T whereEntity) {
        return retBool(autoMapper.updateSelective(entity, whereEntity));
    }

    @Override
    public boolean updateBatchById(List<T> entityList) {
        return retBool(autoMapper.updateBatchById(entityList));
    }

    @Override
    public T selectById(I id) {
        return autoMapper.selectById(id);
    }

    @Override
    public List<T> selectBatchIds(List<I> idList) {
        return autoMapper.selectBatchIds(idList);
    }

    @Override
    public T selectOne(T entity) {
        return autoMapper.selectOne(entity);
    }

    // # 下面2组方法，是orderByField的区别

    @Override
    public List<T> selectList (T entity, String orderByField) {
        return autoMapper.selectList(new EntityWrapper<T>(entity, orderByField));
    }

    @Override
    public List<T> selectList(T entity) {
        return autoMapper.selectList(new EntityWrapper<T>(entity, null));
    }

    // # 下面2组方法，是 orderByField 的区别

    @Override
    public Page<T > selectPage (Page<T> page, T entity, String orderByField) {
        page.setRecords(autoMapper.selectPage(page, new EntityWrapper<T>(entity, orderByField)));
        return page;
    }

    @Override
    public Page<T> selectPage (Page<T> page, T entity) {
        page.setRecords(autoMapper.selectPage(page, new EntityWrapper<T>(entity, null)));
        return page;
    }

}
