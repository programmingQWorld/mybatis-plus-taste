package im.lincq.mybatisplus.taste.test.proxy;

import im.lincq.mybatisplus.taste.test.proxy.mapper.IUserMapper;
import im.lincq.mybatisplus.taste.test.proxy.mapper.User;
import im.lincq.mybatisplus.taste.test.proxy.mapper.UserMapperImpl;

/**
 * <p>
 *     mybatis 执行原理测试
 * </p>
 */
public class TestProxy {

    public static void main ( String[] rags ) {
        IUserMapper userMapper = MapperProxyFactory.getMapper(IUserMapper.class);
        User user = userMapper.selectById(1L);
        userMapper.deleteById(1L);
        System.err.println((user == null) ? "代理失败" : user.getName());
    }
}
