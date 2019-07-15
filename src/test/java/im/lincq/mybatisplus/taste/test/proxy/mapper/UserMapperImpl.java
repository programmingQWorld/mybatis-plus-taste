package im.lincq.mybatisplus.taste.test.proxy.mapper;


/**
 * <p>模拟 mybatis 加载 xml 执行 sql 返回</p>
 */
public class UserMapperImpl implements IUserMapper {
    @Override
    public User selectById(Long id) {
        System.err.println(" ---  执行SQL 绑定数据 ---");
        User user = new User();
        user.setId(id);
        user.setName("mybatis-plus");
        user.setAge(100);
        return user;
    }

    @Override
    public int deleteById(Long id) {
        System.out.println("删除成功");
        return 1;
    }
}
