package im.lincq.mybatisplus.taste.annotations;

/**
 * 字段策略枚举类
 * @authors: lincq
 * @date: 2020/1/10 10:51
 **/
public enum FieldStrategy {

    /** */
    IGNORED(  0,  "ignored"),
    NOT_NULL( 1,  "not null"),
    NOT_EMPTY(0,  "not empty"),
    ;

    /** 主键 */
    private final int key;

    /** 描述 */
    private final String desc;

    FieldStrategy (final int key, final String desc) {
        this.key = key;
        this.desc = desc;
    }

    public int getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }

}
