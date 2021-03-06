package im.lincq.mybatisplus.taste.test.mapper;

import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 继承AutoMapper类，就拥有CRUD方法
 */
public interface UserMapper extends AutoMapper<User> {

    /**
     * 用户列表，分页显示
     *
     * @param pagination  分页对象，原型为 RowBounds，传递参数包含该属性，mybatis-plus分页拦截器会拦截处理该对象
     * @return 查询结果集
     */
    List<User> selectListRow (RowBounds pagination);

    /**
     * 注解插入【测试】
     */
    @Insert("insert into user (test_id, name, age) values (#{id}, #{name}, #{age})")
    int insertInjector(User user);

    /**
     * 自定义注入方法
     */
    int deleteAll();

}
