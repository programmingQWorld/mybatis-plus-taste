package im.lincq.mybatisplus.taste.annotations;

/**
 * @author lincq
 * @date 2019/5/20 23:38
 */
public enum IdType {

    /* ... */
    AUTO("0", "数据库ID自增"),
    INPUT("1", "用户输入ID"),
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
