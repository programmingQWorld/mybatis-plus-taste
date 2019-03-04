package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


import java.io.InputStream;
import java.util.List;

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

        int result = userMapper.deleteByName("test");
        System.out.println("\n------------------deleteByName----------------------\n result=" + result);

        userMapper.insert(new User("test", 18));
        System.out.println("\n------------------insert----------------------\n name=test, age=18");

        /*
		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
		 */
        System.err.println("\n------------------selectById----------------------");
        User user = userMapper.selectById(2L);
        print(user);

        /*
		 * updateById 是从 AutoMapper 中继承而来的，UserMapper.xml中并没有申明改sql
		 */
        System.err.println("\n------------------updateById----------------------");
        user.setName("MybatisPlus_" + System.currentTimeMillis());
        userMapper.updateById(user);

        /*
		 * 此处的 selectById 被UserMapper.xml中的 selectById 覆盖了
		 */
        user = userMapper.selectById(user.getId());
        print(user);

        System.err.println("\n------------------selectAll----------------------");
        List<User> userList = userMapper.selectAll();
        for (int i = 0; i < userList.size(); i++) {
            print(userList.get(i));
        }
        // 提交
        session.commit();
    }
    public static void print(User user){
        System.out.println("名字：" + user.getName() + "年龄：" + user.getAge() + " id" + user.getId());
    }
}

class MyMap<K, V> {
    // 定义一个node数组保存key-value键值对
    private Node<K, V>[] nodes;
    // 逻辑长度
    private int size;

    private static class Node<K, V> {
        K key;
        V value;
        // 构造函数
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // 放入元素
    public void put (K key ,V value) {
        if (nodes == null) {
            // 如果数组为空，创建大小为10的数组
            nodes = new Node[10];
        }
        // 查找key所在数组元素
        int index = indexOfKey(key);
        if (index != -1) {
            nodes[index].value = value;
        } else {
            // 如果没有找到对应的key值，在数组最后添加node
            nodes[size] = new Node(key, value);
            size ++;
        }
    }

    private int indexOfKey (K key) {
        for (int index = 0; index < size; index++) {
            if (key.equals(nodes[index].key)) {
                return index;
            }
        }
        return -1;
    }

    // 根据key获取value值
    public V get(K key) {
        int index = indexOfKey(key);
        if (index != -1) {
            return nodes[index].value;
        }
        return null;
    }

    public int size() {
        return size;
    }


}