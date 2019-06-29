package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.mapper.CommonMapper;

/**
 * <p>主键 String 类型 IService 实现类（ 泛型：M 是 mapper 对象， T 是实体 ）</p>
 *
 * @author lincq
 * @date 2019/6/29 13:12
 */
public class CommonServiceImpl<M extends CommonMapper<T>, T> extends ServiceImpl<M, T, String> {
}