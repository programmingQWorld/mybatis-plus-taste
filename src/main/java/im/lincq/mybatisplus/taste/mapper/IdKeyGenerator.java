package im.lincq.mybatisplus.taste.mapper;


import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;

public class IdKeyGenerator implements KeyGenerator {

    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        System.err.println("---processBefore----");
    }

    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        System.err.println("---processAfter----");
    }
}
