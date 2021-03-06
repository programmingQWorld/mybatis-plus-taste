package im.lincq.mybatisplus.taste.test.mysql.entity;

import im.lincq.mybatisplus.taste.annotations.*;

import java.lang.reflect.Field;

/* 表名 value 注解【 驼峰命名可无 】, resultMap 注解测试【 映射 xml 的 resultMap 内容 】 */
@TableName(value = "user", resultMap = "userMap")
public class User {
    /* 表字段 主键，false 表中不存在的字段，可无该注解 默认 true */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /* 主键ID， auto属性true自增 false非自增 默认true */
    @TableId(value="test_id", type = IdType.ID_WORKER)
    private Long id;

    /* 测试忽略验证 */
    @TableField(validate = FieldStrategy.IGNORED)
    private String name;

    private Integer age;

    /* 测试下划线字段命名类型 */
    @TableField(value = "test_type")
    private Integer testType;

    @TableField(el = "role.id")
    private Role role;

    //或@TableField(el = "role,jdbcType=BIGINT)
    @TableField(el = "phone, typeHandler=im.lincq.mybatisplus.taste.test.mysql.typehandler.PhoneTypeHandler")
    private PhoneNumber phone;

    public User () {
    }
    public User(Long id , Integer testType) {
        this.id = id;
        this.testType = testType;
    }
    public User(Integer testType) {
        this.testType = testType;
    }
    public User(String name) {
        this.name = name;
    }
    public User (Long id, String name) {
        this.id = id;
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
    public User(Long id, String name, int age, Integer testType) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public User(String name, Integer age, Integer testType) {
        this.name = name;
        this.age = age;
        this.testType = testType;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getTestType() {
        return testType;
    }

    public void setTestType(Integer testType) {
        this.testType = testType;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumber phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", testType=" + testType +
                ", role=" + role +
                ", phone=" + phone +
                '}';
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
