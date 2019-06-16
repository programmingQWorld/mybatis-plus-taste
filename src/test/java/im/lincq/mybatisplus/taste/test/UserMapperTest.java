package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
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
 *
 * RUN 测试
 *
 * <p>
 * MybatisPlus 加载 SQL 顺序：
 * </p>
 * 1、加载XML中的SQL<br>
 * 2、加载sqlProvider中的SQL<br>
 * 3、xmlSql 与 sqlProvider不能包含相同的SQL<br>
 * <br>
 * 调整后的SQL优先级：xmlSql > sqlProvider > crudSql
 * <br>
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
        //session.delete("deleteAll");

        /* 插入*/
        System.out.println("\n------------------insert-----------------\n , age=18");
        Long id = IdWorker.getId();
        userMapper.insert(new User(id, "lincq",18));
        sleep();

        List<User> ul = new ArrayList<User>();
        ul.add(new User("insert-batch-1", 12, 0));
        ul.add(new User( "insert-batch-2", 13, 9));
        ul.add(new User( "insert-batch-3", 14, 9));
        ul.add(new User( "delname", 14, 6));
        int rlt = userMapper.insertBatch(ul);

        System.err.println("\n------------------insertBatch----------------------\n \n\n\n" + rlt);
        sleep();


        rlt = 0;

        /* 删除 */
        rlt = userMapper.deleteById(id);
        System.err.println("\n\n---------deleteById------- delete id=" + id + " ,result=" + rlt);
        sleep();

        List<Long> ids = new ArrayList<Long>();
        ids.add(20L);
        ids.add(25L);
        rlt = userMapper.deleteBatchIds(ids);
        System.err.println("\n------------------deleteBatchIds----------------------\n result=" + rlt);

        User deleteSelective = new User();
        deleteSelective.setName("lincq");
        rlt = userMapper.deleteSelective(deleteSelective);
        System.err.println("\n------------------deleteBySelective----------------------\n result=" + rlt);
        sleep();

        /* 修改[updateById 是从 AutoMapper 中继承而来的，UserMapper.xml中并没有申明改sql] */
        User user = new User();
        user.setId(1);
        user.setName("lin-cq :: MybatisPlus_" + System.currentTimeMillis());
        rlt = userMapper.updateById(user);
        System.err.println("\n------------------updateById----------------------\n result=" + rlt);
        sleep();

        /*
		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
		 */
        System.err.println("\n------------------selectById----------------------");
        user = userMapper.selectById(user.getId());
        print(user);

        System.err.println("\n------------------selectOne----------------------");
        User one = userMapper.selectOne(new User("lincq33"));
        print(one);

        System.err.println("\n------------------selectList--------(id DESC)--------------");
        EntityWrapper<User> ew = new EntityWrapper<>(new User("insert-batch-2"), "id desc");
        List<User> ewUserList = userMapper.selectList(RowBounds.DEFAULT, ew);
        ewUserList.forEach(UserMapperTest::print);

        System.err.println("\n------------------selectBatchIds----------------------");
        List<Object> idList = new ArrayList<Object>();
        idList.add(11L);
        idList.add(12L);
        List<User> ul1 = userMapper.selectBatchIds(idList);
        ul1.forEach(UserMapperTest::print);

        System.err.println("\n------------------分页pagination查询 --- 查询页中 testType = 1 的所有数据----------------------");
        Pagination pagination = new Pagination(1, 2);
        List<User> paginList = userMapper.selectList(pagination, ew);
        paginList.forEach(UserMapperTest::print);
        System.err.println("翻页： " + pagination.toString());

        System.err.println("\n---------------xml---selectListRow 分页查询，不查询总数（此时可自定义 count 查询）----无查询条件--------------");
        //List<User> rowList = userMapper.selectListRow(new RowBounds(0, 2));
        //rowList.forEach(UserMapperTest::print);

        /* 删除测试数据 */
        //rlt = session.delete("deleteAll");
        System.err.println("清空测试数据！ rlt=" + rlt);

        System.err.println("\n------------------insertBatch----------------------\n \n\n\n" + userMapper.insertBatch(ul));
        sleep();

        // 提交
        session.commit();
    }

    /**
     * 打印测试信息
     * @param user 实体
     */
    private static void print(User user){
        sleep();
        if (user == null) {
            System.out.println("UserMapperTest::print()::当前传入参数为空，无法正常输出");
            return;
        }
        System.out.println("名字：" + user.getName() + "年龄：" + user.getAge() + " id" + user.getId() + "test_type：" + user.getTestType());
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