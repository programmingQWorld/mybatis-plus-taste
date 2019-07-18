package im.lincq.mybatisplus.taste.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;


/**
 * <p>
 *     高效GUID产生算法(sequence)，基于Snowflake实现64位自增ID算法。<br>
 *     优化开源项目 http://git.oschina.net/yu120/sequence
 * </p>
 */
public class IdWorker {
    /**
     * 主机和进程的机器码
     */
    private static Sequence worker = new Sequence();

    public static long getId () {
        return worker.nextId();
    }

}
