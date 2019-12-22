package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.test.mapper.UserMapper;
import im.lincq.mybatisplus.taste.test.mysql.MySqlInjector;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;
import im.lincq.mybatisplus.taste.toolkit.IdWorker;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.InputStream;

/**
 * 事务测试
 * @author lincq
 * @date 2019/12/22 13:26
 */
public class TransactionalTest {

    /**
     * 事务测试
     * @param args
     */
    public static void main(String[] args) {
        /* 加载配置文件 */
        InputStream in = TransactionalTest.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
        mf.setSqlInjector(new MySqlInjector());
        SqlSessionFactory sessionFactory = mf.build(in);
        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        userMapper.deleteAll();

        /* 插入 */
        int rlt = userMapper.insertInjector(new User(IdWorker.getId(), "1", 1, 1));
        System.err.print("--------- insert injector -------------");
        session.commit();

    }
}
