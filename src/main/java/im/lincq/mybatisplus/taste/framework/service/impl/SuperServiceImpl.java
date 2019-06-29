package im.lincq.mybatisplus.taste.framework.service.impl;

import im.lincq.mybatisplus.taste.mapper.AutoMapper;

/**
 * 主键 Long 类型 IService 实现类  （泛型: M是Mapper对象，T是实体）
 *
 * @author lincq
 * @date 2019/6/16 23:09
 */
public class SuperServiceImpl<M extends AutoMapper<T>, T> extends ServiceImpl<M, T, Long> {
}
