package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.plugins.pagination.Pagination;
import im.lincq.mybatisplus.taste.test.entity.User;
import im.lincq.mybatisplus.taste.test.mapper.UserMapper;
import im.lincq.mybatisplus.taste.toolkit.IdWorker;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatisPlus 测试类
 */
public class UserMapperTest {
    private static final String RESOUCE = "mybatis-config.xml";

    public static void main(String[] args) {

        // 使用缺省Log4j环境
        BasicConfigurator.configure();
        System.out.println("IdWorker.getId(): " + IdWorker.getId());

        InputStream in = UserMapperTest.class.getClassLoader().getResourceAsStream(RESOUCE);
        // SqlSession session = new SqlSessionFactoryBuilder().build(in).openSession();
        // 此处使用MybatisSessionFactoryBuilder构建SqlSessionFactory,目的是为了引入AutoMapper
        SqlSessionFactory sessionFactory = new MybatisSessionFactoryBuilder().build(in);
        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);

        System.err.println("\n------------------deleteByIds----------------------\n");
        List<Long> idList = new ArrayList<Long>();
        idList.add(101L);
        idList.add(102L);
        idList.add(103L);
        int rlt = userMapper.deleteByIds(idList);
        System.out.println(rlt);

        System.err.println("\n------------------insertBatch----------------------\n");
        List<User> ul = new ArrayList<User>();
        ul.add(new User(IdWorker.getId(), "insert-batch-1", 12));
        ul.add(new User(IdWorker.getId(), "insert-batch-2", 13));
        ul.add(new User(IdWorker.getId(), "insert-batch-3", 14));
        int rtl = userMapper.insertBatch(ul);
        System.out.println(rtl);

        int n = userMapper.insert(new User(IdWorker.getId(), "6", 6));
        System.out.println(n);

//        int result = userMapper.deleteByName("test");
//        System.err.println("\n------------------deleteByName----------------------\n result=" + result);

//        System.out.println("\n------------------insert----------------------\n name=test, age=18");
//        Long id = IdWorker.getId();
//        userMapper.insert(new User(id, "new lincq", 18));

//        /*
//		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
//		 */
//        System.err.println("\n------------------selectById----------------------");
//        User user = userMapper.selectById(1L);
//        print(user);
//
//        /*
//		 * updateById 是从 AutoMapper 中继承而来的，UserMapper.xml中并没有申明改sql
//		 */
//        System.err.println("\n------------------updateById----------------------");
//        user.setName("MybatisPlus_" + System.currentTimeMillis());
//        userMapper.updateById(user);
//        /*
//		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
//		 */
//        user = userMapper.selectById(user.getId());
//        print(user);
//
//        System.err.println("\n------------------selectAll----------------------");
//        List<User> userList = userMapper.selectAll();
//        userList.forEach(UserMapperTest::print);
//
//        System.err.println("\n------------------list 分页查询，不查询总数（此时可自定义 count 查询）----------------------");
//        List<User> rowList = userMapper.list(new RowBounds(0, 5));
//        rowList.forEach(UserMapperTest::print);
//
//        System.err.println("\n------------------list 分页查询，查询总数----------------------");
//        Pagination pagination = new Pagination(0, 2);
//        List<User> pageList = userMapper.list(pagination);
//        pageList.forEach(UserMapperTest::print);
//        System.out.println("本次查询的总数是:" + pagination.getTotal());
//        System.out.println(pagination);
//
//        System.err.println("\n\n------------------deleteById----------------------");
//        int del = userMapper.deleteById(id);
//        System.out.println(" delete id=" + id + " ,result=" + del);

        // 提交
        session.commit();
    }

    /**
     * 打印测试信息
     * @param user 实体
     */
    private static void print(User user){
        sleep();
        System.out.println("名字：" + user.getName() + "年龄：" + user.getAge() + " id" + user.getId());
    }

    /**
     * 慢点打印
     */
    private static void sleep () {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}