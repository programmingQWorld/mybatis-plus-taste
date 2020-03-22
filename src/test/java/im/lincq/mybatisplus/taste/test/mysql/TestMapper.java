package im.lincq.mybatisplus.taste.test.mysql;

import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;

/**
 * 继承 AutoMapper，就自动拥有CRUD方法
 * @authors: lincq
 * @date: 2020/3/22 23:59
 **/
public interface TestMapper extends AutoMapper<User> {

}
