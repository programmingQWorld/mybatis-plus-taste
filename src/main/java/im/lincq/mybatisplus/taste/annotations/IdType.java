package im.lincq.mybatisplus.taste.annotations;

/**
 * @author lincq
 * @date 2019/5/20 23:38
 */
public enum IdType {

    /* ... */
    AUTO("0", "数据库ID自增"),
    /** 如果插入对象ID 为空，才自动填充。*/
    ID_WORKER("1", "全局唯一ID"),
    INPUT("2", "用户输入ID"),
    ;

    /** 主键 */
    private final String key;

    /** 描述 */
    private final String desc;

    public String getKey () {
        return this.key;
    }

    public String getDesc () {
        return this.desc;
    }

    IdType(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
