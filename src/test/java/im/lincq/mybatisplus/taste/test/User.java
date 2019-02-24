package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.annotation.Id;
import im.lincq.mybatisplus.taste.annotation.Table;

@Table(name="user")
public class User {
    // 主键ID 注解，auto 属性 true 自增，false 非自增
    @Id
    private long id;
    private String name;
    private int age;

    public long getId() {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
