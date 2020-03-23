package im.lincq.mybatisplus.taste.test.mysql.entity;

import im.lincq.mybatisplus.taste.annotations.TableField;
import im.lincq.mybatisplus.taste.annotations.TableId;
import im.lincq.mybatisplus.taste.annotations.TableName;

import java.io.Serializable;

/**
 * 测试没有XML同样注入CRUD SQL 实体
 * @authors: lincq
 * @date: 2020/3/22 23:56
 **/
@TableName("test")
public class Test implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String type;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
