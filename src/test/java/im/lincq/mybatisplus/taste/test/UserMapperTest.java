package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


import java.io.InputStream;

/**
 * MyBatisPlus 测试类
 */
public class UserMapperTest {
    private static final String RESOUCE = "mybatis-config.xml";

    public static void main(String[] args) {

        InputStream in = UserMapperTest.class.getClassLoader().getResourceAsStream(RESOUCE);
        // SqlSession session = new SqlSessionFactoryBuilder().build(in).openSession();
        // 此处使用MybatisSessionFactoryBuilder构建SqlSessionFactory,目的是为了引入AutoMapper
        SqlSessionFactory sessionFactory = new MybatisSessionFactoryBuilder().build(in);
        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user = userMapper.selectById(2L);
        print(user);
        user.setName("新名字." + System.currentTimeMillis());
        userMapper.updateById(user);
        session.commit();
    }
    public static void print(User user){
        System.out.println("名字：" + user.getName() + "年龄：" + user.getAge() + " id" + user.getId());
    }
}