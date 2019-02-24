package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.mapper.AutoMapper;

/**
 * 继承AutoMapper类，就拥有CRUD方法
 */
public interface UserMapper extends AutoMapper<User> {
}
