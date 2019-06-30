package im.lincq.mybatisplus.taste.mapper;

public enum SqlMethod {
    /**
     * 增加
     */
    INSERT_ONE("insert", "插入一条数据", "<script>INSERT INTO %s %s VALUES %s</script>"),
    INSERT_ONE_SELECTIVE("insertSelective", "插入一条数据(选择字段, null字段不插入)", "<script>INSERT INTO %s %s VALUES %s</script>"),
    INSERT_BATCH_MYSQL("insertBatch", "mysql 批量插入数据", "<script>INSERT INTO %s %s VALUES \n <foreach item = \"item\" index = \"index\" collection = \"list\" separator = \",\">%s\n</foreach></script>"),
    INSERT_BATCH_ORACLE("insertBatch", "oracle 批量插入数据", "<script>INSERT INTO %s %s \n <foreach item = \"item\" index = \"index\" collection = \"list\" separator = \"UNION ALL\">%s\n</foreach></script>"),
    /**
     * 删除
     */
    DELETE_BY_ID("deleteById", "根据ID 删除一条数据", "DELETE FROM %s WHERE %s = #{%s}"),
    DELETE_SELECTIVE("deleteSelective", "根据 entity 条件删除记录", "<script>DELETE FROM %s %s</script>"),
    DELETE_BATCH("deleteBatchIds", "根据ID集合，批量删除数据", "<script>DELETE FROM %s WHERE %s IN (%s)</script>"),

    /**
     * 修改
     */
    UPDATE_BY_ID("updateById", "根据ID 修改数据", "<script>UPDATE %s %s  WHERE %s=#{et.%s}</script>"),
    UPDATE_BATCH_BY_ID_MYSQL("updateBatchById", "mysql 根据ID 批量修改数据", "<script>UPDATE %s %s WHERE %s IN (<foreach collection=\"list\" separator=\",\" item=\"i\" index=\"index\">#{i.%s}</foreach>)</script>"),
    UPDATE_BATCH_BY_ID_ORACLE("updateBatchById", "oracle 根据ID 批量修改数据", "<script><foreach collection=\"list\" item=\"item\" index=\"index\" open=\"BEGIN\" close=\"END;\" separator=\";\">UPDATE %s %s WHERE %s=#{item.%s}</foreach></script>"),
    UPDATE_SELECTIVE_BY_ID("updateSelectiveById", "根据ID 选择修改数据", "<script>UPDATE %s %s WHERE %s=#{et.%s}</script>"),
    UPDATE("update", "根据 whereEntity 条件，更新记录", "<script>UPDATE %s %s %s</script>"),
    UPDATE_SELECTIVE("updateSelective", "根据 whereEntity 条件，选择更新记录", "<script>UPDATE %s %s %s</script>"),

    /**
     * 查询
     */
    SELECT_BY_ID("selectById", "根据ID 查询一条数据", "SELECT %s FROM %s WHERE %s = #{ID}"),
    SELECT_BATCH("selectBatchIds", "根据ID集合，批量查询数据", "<script>SELECT %s FROM %s WHERE %s IN (%s)</script>"),
    SELECT_ONE("selectOne", "查询满足条件一条数据", "<script>SELECT %s FROM %s %s</script>"),
    SELECT_COUNT("selectCount", "查询满足条件总记录数", "<script>SELECT COUNT(1) FROM %s %s</script>"),
    SELECT_LIST("selectList", "查询满足条件所有数据", "<script>SELECT %s FROM %s %s</script>"),
    SELECT_PAGE("selectPage", "查询满足条件所有数据（并翻页）", "<script>SELECT %s FROM %s %s</script>");

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
