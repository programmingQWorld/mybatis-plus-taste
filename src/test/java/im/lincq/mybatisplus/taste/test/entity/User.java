package im.lincq.mybatisplus.taste.test.entity;

import im.lincq.mybatisplus.taste.annotations.TableField;
import im.lincq.mybatisplus.taste.annotations.TableId;
import im.lincq.mybatisplus.taste.annotations.TableName;

import java.lang.reflect.Field;

@TableName(value="user")
public class User {
    /* 表字段 主键，false 表中不存在的字段，可无该注解 默认 true */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /* 主键ID， auto属性true自增 false非自增 默认true */
    @TableId(value="test_id", auto = false)
    private Long id;
    private String name;
    private Integer age;

    /* 测试下划线字段命名类型 */
    //@TableField(value = "test_type")
    //private Integer testType;


    public User () {
    }
    public User(String name) {
        this.name = name;
    }
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public User(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }



    /**
     * 测试类型
     */
    public static void main(String[] args) throws IllegalAccessException {
        User user = new User();
        user.setName("123456");
        user.setAge(3);
        System.err.println(User.class.getSimpleName());
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println("===================================");
            System.out.println(field.getName());
            System.out.println(field.getType().toString());
            field.setAccessible(true);
            System.out.println(field.get(user)); // 获取变量的值
        }
    }
}
