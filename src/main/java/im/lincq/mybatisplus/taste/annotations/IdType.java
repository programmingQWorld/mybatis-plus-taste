package im.lincq.mybatisplus.taste.annotations;

/**
 * @author lincq
 * @date 2019/5/20 23:38
 */
public enum IdType {

    /* ... */
    AUTO(0, "数据库ID自增"),
    INPUT(1, "用户输入ID"),

    /** 如果插入对象ID 为空，才自动填充。*/
    ID_WORKER(2, "全局唯一ID"),
    UUID(3, "全局UUID"),

    ;

    /** 主键 */
    private final int key;

    /** 描述 */
    private final String desc;

    public int getKey () {
        return this.key;
    }

    public String getDesc () {
        return this.desc;
    }

    IdType(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
