package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.framework.service.IService;
import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.mapper.BaseMapper;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author lincq
 * @date 2019/6/29 13:05
 */
public class ServiceImpl<M extends BaseMapper<T, I>, T, I> implements IService<T, I> {

    @Autowired
    protected M baseMapper;

    public boolean retBool (int result) {
        return result >= 1;
    }

    @Override
    public boolean insert(T entity) {
        return retBool(baseMapper.insert(entity));
    }

    @Override
    public boolean insertBatch (List<T> entityList) {
        return retBool(baseMapper.insertBatch(entityList));
    }

    @Override
    public boolean insertSelective(T entity) {
        return retBool(baseMapper.insertSelective(entity));
    }

    @Override
    public boolean deleteById(I id) {
        return retBool(baseMapper.deleteById(id));
    }

    @Override
    public boolean deleteByMap(Map<String, Object> columnMap) {
        return retBool(baseMapper.deleteByMap(columnMap));
    }

    @Override
    public boolean deleteSelective(T entity) {
        return retBool(baseMapper.deleteSelective(entity));
    }

    @Override
    public boolean deleteBatchIds(List<I> idList) {
        return retBool(baseMapper.deleteBatchIds(idList));
    }

    @Override
    public boolean updateById (T entity) {
        return retBool(baseMapper.updateById(entity));
    }

    @Override
    public boolean updateSelectiveById(T entity) {
        return retBool(baseMapper.updateSelectiveById(entity));
    }

    @Override
    public boolean update(T entity, T whereEntity) {
        return retBool(baseMapper.update(entity, whereEntity));
    }

    @Override
    public boolean updateSelective(T entity, T whereEntity) {
        return retBool(baseMapper.updateSelective(entity, whereEntity));
    }

    @Override
    public boolean updateBatchById(List<T> entityList) {
        return retBool(baseMapper.updateBatchById(entityList));
    }

    @Override
    public T selectById(I id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<T> selectBatchIds(List<I> idList) {
        return baseMapper.selectBatchIds(idList);
    }

    @Override
    public List<T> selectByMap(Map<String, Object> columnMap) {
        return baseMapper.selectByMap(columnMap);
    }

    @Override
    public T selectOne(T entity) {
        return baseMapper.selectOne(entity);
    }

    @Override
    public int selectCount(T entity) {
        return baseMapper.selectCount(entity);
    }

    @Override
    public List<T> selectList(EntityWrapper<T> entityWrapper) {
        return baseMapper.selectList(entityWrapper);
    }

    // 把 order by field 变量给搞到了分页page里面去了，怪不得看不到这个变量了
    @Override
    public Page<T> selectPage(Page<T> page, EntityWrapper<T> entityWrapper) {
        page.setRecords(baseMapper.selectPage(page, entityWrapper));
        return page;
    }

    protected String convertSqlSegment(String sqlSegment, String orderByField, boolean isAsc) {
        StringBuffer segment = new StringBuffer();
        if (null != sqlSegment) {
            segment.append(sqlSegment);
        }
        if (null != orderByField) {
            segment.append(" ORDER BY  ").append(orderByField);
            if (!isAsc) {
                segment.append(" DESC ");
            }
        }
        return segment.toString();
    }

}
