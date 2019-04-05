package im.lincq.mybatisplus.taste.mapper;

public enum SqlMethod {
    /**
     * 增加
     */
    INSERT_ONE("insert", "插入一条数据", "INSERT INTO %s (%s) VALUES (%s)"),
    INSERT_BATCH("insertBatch", "批量插入数据", "<script>INSERT INTO %s (%s) VALUES %s</script>"),

    /**
     * 删除
     */
    DELETE_ONE("deleteById", "根据ID 删除一条数据", "DELETE FROM %s WHERE %s = #{ID}"),
    DELETE_BATCH("deleteBatchIds", "根据ID集合，批量删除数据", "<script>DELETE FROM %s WHERE %s IN (%s)</script>"),

    /**
     * 修改
     */
    UPDATE_ONE("updateById", "根据ID 修改数据", "<script>UPDATE %s %s</script>"),

    /**
     * 查询
     */
    SELECT_ONE("selectById", "根据ID 查询一条数据", "SELECT * FROM %s WHERE %s = #{ID}"),
    SELECT_BATCH("selectBatchIds", "根据ID集合，批量查询数据", "<script>SELECT * FROM %s WHERE %s IN (%s)</script>"),
    SELECT_ALL("selectAll", "查询满足条件所有数据", "SELECT * FROM %s");

    private final String method;
    private final String desc;
    private final String sql;

    SqlMethod(final String method, final String desc, final String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }
}
