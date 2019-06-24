package im.lincq.mybatisplus.taste.mapper;

/**
 * @author lincq
 * @date 2019/6/24 23:41
 */
public enum DBType {

    /* ... */
    MYSQL("mysql", "MySql数据库"),ORACLE("oracle", "Oracle数据库");

    /** 数据库名称 */
    private final String db;

    /** 描述 */
    private final String desc;

    public static DBType getDBType (String dbType) {
        for (DBType dt : DBType.values()) {
            if (dt.getDb().equals(dbType)) {
                return dt;
            }
        }
        return MYSQL;
    }

    DBType (final String db, final String desc) {
        this.db = db;
        this.desc = desc;
    }

    public String getDb() {
        return db;
    }

    public String getDesc() {
        return desc;
    }
}
