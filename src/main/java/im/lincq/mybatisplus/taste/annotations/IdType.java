package im.lincq.mybatisplus.taste.annotations;

/**
 * @author lincq
 * @date 2019/5/20 23:38
 */
public enum IdType {

    /* ... */
    AUTO_INCREMENT("0", "数据库ID自增"),
    ID_INPUT("1", "用户输入ID"),
    ID_WORKER("2", "IdWorkerKeyGenerator生成全局唯一ID工具类");
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
