package im.lincq.mybatisplus.taste.test.mysql.entity;

import im.lincq.mybatisplus.taste.annotations.TableField;
import im.lincq.mybatisplus.taste.annotations.TableId;
import im.lincq.mybatisplus.taste.annotations.TableName;

import java.io.Serializable;

/**
 * @author lincq
 * @date 2019/12/29 22:08
 */
@TableName(value = "role", resultMap = "RoleMap")
public class Role implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId
    private long id;

    /** 角色 */
    private String name;

    /** 排序 */
    private int sort;

    /** 描述 */
    private String description;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", description='" + description + '\'' +
                '}';
    }
}
