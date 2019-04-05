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

        // 加载配置文件
        InputStream in = UserMapperTest.class.getClassLoader().getResourceAsStream(RESOUCE);

        // SqlSession session = new SqlSessionFactoryBuilder().build(in).openSession();
        // 此处使用MybatisSessionFactoryBuilder构建SqlSessionFactory,目的是为了引入AutoMapper
        SqlSessionFactory sessionFactory = new MybatisSessionFactoryBuilder().build(in);
        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        System.err.println(" debug run 查询执行 user 表数据变化！ ");

        /* 插入*/
        System.out.println("\n------------------insert----------------------\n name=test, age=18");
        Long id = IdWorker.getId();
        userMapper.insert(new User(id, "new lincq", 18));
        sleep();

        List<User> ul = new ArrayList<User>();
        ul.add(new User(IdWorker.getId(), "insert-batch-1", 12));
        ul.add(new User(IdWorker.getId(), "insert-batch-2", 13));
        ul.add(new User(IdWorker.getId(), "insert-batch-3", 14));
        ul.add(new User(IdWorker.getId(), "delname", 14));
        int rlt = userMapper.insertBatch(ul);
        System.err.println("\n------------------insertBatch----------------------\n result=" + rlt);
        sleep();

        /* 删除 */
        rlt = userMapper.deleteById(id);
        System.err.println("\n\n---------deleteById------- delete id=" + id + " ,result=" + rlt);
        sleep();

        List<Long> ids = new ArrayList<Long>();
        ids.add(20L);
        ids.add(25L);
        rlt = userMapper.deleteBatchIds(ids);
        System.err.println("\n------------------deleteBatchIds----------------------\n result=" + rlt);

        rlt = userMapper.deleteByName("delname");
        System.err.println("\n------------------deleteByName----------------------\n result=" + rlt);
        sleep();

        /* 修改[updateById 是从 AutoMapper 中继承而来的，UserMapper.xml中并没有申明改sql] */
        User user = userMapper.selectById(12L);
        user.setName("lin-cq :: MybatisPlus_" + System.currentTimeMillis());
        rlt = userMapper.updateById(user);
        System.err.println("\n------------------updateById----------------------\n result=" + rlt);
        sleep();

        /*
		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
		 */
        user = userMapper.selectById(user.getId());
        print(user);

        System.err.println("\n------------------selectBatchIds----------------------");
        List<Object> idList = new ArrayList<Object>();
        idList.add(11L);
        idList.add(12L);
        List<User> ul1 = userMapper.selectBatchIds(idList);
        ul1.forEach(UserMapperTest::print);

        System.err.println("\n------------------selectAll----------------------");
        List<User> userList = userMapper.selectAll();
        userList.forEach(UserMapperTest::print);

        System.err.println("\n------------------list 分页查询，查询总数----------------------");
        Pagination pagination = new Pagination(0, 2);
        List<User> pageList = userMapper.list(pagination);
        pageList.forEach(UserMapperTest::print);
        System.out.println("本次查询的总数是:" + pagination.getTotal());
        System.out.println(pagination);

        /* 删除测试数据 */
        rlt = session.delete("deleteAll");
        System.err.println("清空测试数据！ rlt=" + rlt);

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