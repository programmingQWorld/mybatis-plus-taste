package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.plugins.Page;
import im.lincq.mybatisplus.taste.plugins.PaginationInterceptor;
import im.lincq.mybatisplus.taste.plugins.pagination.Pagination;
import im.lincq.mybatisplus.taste.test.mysql.MySqlInjector;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;
import im.lincq.mybatisplus.taste.test.mapper.UserMapper;
import im.lincq.mybatisplus.taste.toolkit.IdWorker;
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
        // 此处使用MybatisSessionFactoryBuilder构建SqlSessionFactory,目的是为了引入AutoMapper（BaseMapper）
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
        SqlSessionFactory sessionFactory = mf.build(in);

        /**
         * 1、数据库字段驼峰命名不需要任何设置
         * 2、当前演示时驼峰下划线混合命名
         * 3、如下开启，标识数据库字段使用下划线命名，该设置是全局的。
         * 开启该设置实体可无 @TableId(value="id") 或者 @TableField(value = "field_1")等字段映射
         * 开启方式为 调用 sqlSessionFactory 实例的 setDbColumnUnderline 方法, 传 true， 如：
         * mf.setDbColumnUnderline(true);
         *
         */

        /**
         * 设置，自定义 SQL 注入器
         */
        //mf.setSqlInjector(new MySqlInjector());

        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        System.err.println(" debug run 查询执行 user 表数据变化！ ");
        //session.delete("deleteAll");

        userMapper.deleteSelective(new User());
        System.err.println("deleteAll --data-- ");

        int rlt = userMapper.insertInjector(new User(1L, "1", 1, 1));
        System.err.println("-----------------insertInjector------------------------" + rlt);


        /**
         * ehcache 缓存测试
         */
        User cacheUserQuery = new User();
        cacheUserQuery.setAge(1);
        cacheUserQuery.setId(1L);
        User cacheUser = userMapper.selectOne(cacheUserQuery);
        print(cacheUser);
        System.out.println("在这里检查查询前后sql执行情况");
        cacheUser = userMapper.selectOne(cacheUserQuery);
        print(cacheUser);

        /* 插入*/
        System.out.println("\n------------------insert----------------- age=18");
        Long id = IdWorker.getId();
        rlt = userMapper.insert(new User(id, "lincq",18, 12));
        sleep();

        rlt = userMapper.insertSelective(new User("abc", 18));
        System.err.println("\n--------------insertSelective-------" + rlt);
        sleep();


        List<User> ul = new ArrayList<User>();
        ul.add(new User("insert-batch-1", 12, 1));
        ul.add(new User( "insert-batch-2", 13, 1));
        ul.add(new User( "insert-batch-3", 14, 1));
        ul.add(new User( "delname", 14, 1));
        rlt = userMapper.insertBatch(ul);

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
        rlt = userMapper.updateSelectiveById(new User(3347303938708733957L, "MybatisPlus", 123, 1));
        System.err.println("------------------updateSelectiveById---------------------- result=" + rlt + "\n\n");
        sleep();

        User user = new User();
        user.setId(1);
        user.setAge(21);
        user.setTestType(1);
        user.setName("lin-cq :: MybatisPlus_" + System.currentTimeMillis());
        rlt = userMapper.updateById(user);
        System.err.println("\n------------------updateById----------------------\n result=" + rlt);
        sleep();

        rlt = userMapper.update(new User("55", 55, 5), new User(15L, "5"));
        System.err.println("------------------update---------------------- result=" + rlt + "\n\n");
        sleep();

        rlt = userMapper.updateSelective(new User("00"), new User(15L, "55"));
        System.err.println("------------------updateSelective---------------------- result=" + rlt + "\n\n");
        sleep();

        /* 无条件选择更新 */
        //userMapper.updateSelective(new User("11"), null);

        List<User> userList = new ArrayList<User>();
        userList.add(new User(11L, "updateBatchById-1", 1, 1));
        userList.add(new User(12L, "updateBatchById-2", 2, 1));
        userList.add(new User(13L, "updateBatchById-3", 3, 1));
        rlt = userMapper.updateBatchById(userList);
        System.err.println("------------------updateBatchById---------------------- result=" + rlt + "\n\n");
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
        /** 查询条件，支持 sql 片段 */
        ew.setSqlSegment(" AND name like '%dateBatch%' ");
        List<User> ewUserList = userMapper.selectList(ew);
        ewUserList.forEach(UserMapperTest::print);

        System.err.println("\n------------------selectBatchIds----------------------");
        List<Long> idList = new ArrayList<>();
        idList.add(11L);
        idList.add(12L);
        List<User> ul1 = userMapper.selectBatchIds(idList);
        ul1.forEach(UserMapperTest::print);

        System.err.println("\n------------------分页pagination查询 --- 查询页中 testType = 1 的所有数据----------------------");
        Page<User> page = new Page<>(1, 2);
        /**
         * 排序 test_id desc
         */
        page.setOrderByField("test_id");
        page.setAsc(false);
        ew = new EntityWrapper<User>(new User(1));

        ew.setSqlSegment("age,name");

        /**
         * 查询条件，SQL片段（注意！程序会自动在 sqlSegment内容前面添加where或者and）
         */
        ew.setSqlSegment(" and name like '%dateBatch%'");
        List<User> paginList = userMapper.selectPage(page, ew);
        paginList.forEach(UserMapperTest::print);

        System.err.println("\n---------------xml---selectListRow 分页查询，不查询总数（此时可自定义 count 查询）----无查询条件--------------");

        paginList = userMapper.selectList(new EntityWrapper<>(null , null, null));
        paginList.forEach(UserMapperTest::print);
        System.err.println("\n----------用户列表-------------");
        //List<User> rowList = userMapper.selectListRow(new RowBounds(0, 2));
        //rowList.forEach(UserMapperTest::print);

        /* 自定义方法，删除测试数据 */
       //rlt = userMapper.deleteAll();
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