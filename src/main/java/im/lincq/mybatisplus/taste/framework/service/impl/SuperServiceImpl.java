package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.framework.service.ISuperService;
import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 抽象Service实现类，（泛型: M是Mapper对象，T是实体，I是实体表id类型）
 *
 * @author lincq
 * @date 2019/6/16 23:09
 */
public class SuperServiceImpl<M extends AutoMapper<T>, T> implements ISuperService<T> {

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
    public boolean deleteById(Long id) {
        return retBool(autoMapper.deleteById(id));
    }

    @Override
    public boolean deleteSelective(T entity) {
        return retBool(autoMapper.deleteSelective(entity));
    }

    @Override
    public boolean deleteBatchIds(List<Long> idList) {
        return retBool(autoMapper.deleteBatchIds(idList));
    }

    @Override
    public boolean updateById (T entity) {
        return retBool(autoMapper.updateById(entity));
    }

    @Override
    public T selectById(Long id) {
        return autoMapper.selectById(id);
    }

    @Override
    public List<T> selectBatchIds(List<Long> idList) {
        return autoMapper.selectBatchIds(idList);
    }

    @Override
    public T selectOne(T entity) {
        return autoMapper.selectOne(entity);
    }

    @Override
    public List<T> selectList(RowBounds rowBounds, EntityWrapper<T> entityWrapper) {
        return autoMapper.selectList(rowBounds, entityWrapper);
    }

}
