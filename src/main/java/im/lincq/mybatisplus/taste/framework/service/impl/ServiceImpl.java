package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;
import im.lincq.mybatisplus.taste.framework.service.IService;
import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.mapper.BaseMapper;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import im.lincq.mybatisplus.taste.toolkit.ReflectionKit;
import im.lincq.mybatisplus.taste.toolkit.StringUtils;
import im.lincq.mybatisplus.taste.toolkit.TableInfo;
import im.lincq.mybatisplus.taste.toolkit.TableInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author lincq
 * @date 2019/6/29 13:05
 */
public class ServiceImpl<M extends BaseMapper<T, PK>, T, PK extends Serializable> implements IService<T, PK> {

    @Autowired
    protected M baseMapper;

    public boolean retBool (int result) {
        return result >= 1;
    }

    /**
     * TableId 注解存在 → 更新记录， 否则插入一条记录
     * @param entity 实体对象
     * @param isSelective true → 选择非null值字段 | false → 不选择字段
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    private boolean insertOrUpdate (T entity, boolean isSelective) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);

            if (null != tableInfo) {
                Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
                if (null != idVal) {
                    return isSelective ? updateSelectiveById(entity) : updateById(entity);
                } else {
                    return isSelective ? insertSelective(entity) : insert(entity);
                }
            } else {
                throw new MybatisPlusException("Error: Cannot execute. Could not find @TableId");
            }

        }
        return false;
    }

    @Override
    public boolean insertOrUpdate (T entity) {
        return insertOrUpdate(entity, false);
    }

    @Override
    public boolean insertOrUpdateSelective(T entity) {
        return insertOrUpdate(entity, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(T entity) {
        return retBool(baseMapper.insert(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch (List<T> entityList) {
        return retBool(baseMapper.insertBatch(entityList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSelective(T entity) {
        return retBool(baseMapper.insertSelective(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(PK id) {
        return retBool(baseMapper.deleteById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMap(Map<String, Object> columnMap) {
        return retBool(baseMapper.deleteByMap(columnMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSelective(T entity) {
        return retBool(baseMapper.deleteSelective(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchIds(List<PK> idList) {
        return retBool(baseMapper.deleteBatchIds(idList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById (T entity) {
        return retBool(baseMapper.updateById(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelectiveById(T entity) {
        return retBool(baseMapper.updateSelectiveById(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(T entity, T whereEntity) {
        return retBool(baseMapper.update(entity, whereEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelective(T entity, T whereEntity) {
        return retBool(baseMapper.updateSelective(entity, whereEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(List<T> entityList) {
        return retBool(baseMapper.updateBatchById(entityList));
    }

    @Override
    public T selectById(PK id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<T> selectBatchIds(List<PK> idList) {
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
        if (entityWrapper != null) {
            entityWrapper.orderBy(page.getOrderByField(), page.isAsc());
        }
        page.setRecords(baseMapper.selectPage(page, entityWrapper));
        return page;
    }

}
