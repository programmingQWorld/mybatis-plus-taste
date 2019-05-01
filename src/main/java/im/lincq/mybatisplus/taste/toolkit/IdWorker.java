package im.lincq.mybatisplus.taste.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

public class IdWorker {
    // 根据具体机器环境提供
    private final long workerId;
    // 滤波器，使时间变小，生成的总位数变小，一旦确定不能变动
    private final static long twepoch = 1361753741828L;
    private long sequence = 0L;
    private final static long workerIdBits = 10L;
    // 值好像是0..
    private final static long maxWorkerId = -1L ^ -1L << workerIdBits;
    private final static long sequenceBits = 12L;
    private final static long workerIdShift = sequenceBits;
    private final static long timestampLeftShift = sequenceBits + workerIdShift;
    private final static long sequenceMask = -1L ^ -1L << sequenceBits;

    private long lastTimestamp = -1L;

    // 根据主机id获取机器码
    private static final IdWorker worker = new IdWorker();
    private static final Logger logger = LoggerFactory.getLogger(IdWorker.class);

    // 主机和进程的机器码
    private static final int _genmachine;
    static {

        try {
            // build a 2-byte machine piece based on NICs info.
            int machinePiece;
            {
                StringBuilder sb = new StringBuilder();
                try {
                    Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                    while (e.hasMoreElements()) {
                        NetworkInterface ni = e.nextElement();
                        sb.append(ni.toString());
                    }
                    machinePiece = sb.toString().hashCode() << 16;
                } catch (Throwable e) {
                    // exception sometimes happens with IBM JVM, use random
                    logger.error(e.getMessage(), e);
                    machinePiece = new Random().nextInt() << 16;
                    e.printStackTrace();
                    logger.debug("machine piece post : " + Integer.toHexString(machinePiece));
                }
            }


            // add 2 bytes process piece. It must represent not only the JVM
            // but the class loader
            // Since static var to the class loader there could be collisions.
            // otherwise
            final int processPiece;
            {
                int processId = new java.util.Random().nextInt();
                processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
                ClassLoader loader = worker.getClass().getClassLoader();
                int loaderId = loader != null ? System.identityHashCode(loader) : 0;

                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toHexString(processId));
                sb.append(Integer.toHexString(loaderId));
                processPiece = sb.toString().hashCode() & 0xFFFF;
                logger.debug("process piece: " + Integer.toHexString(processPiece));

            }

            _genmachine = machinePiece | processPiece;
            logger.debug("machine : " + Integer.toHexString(_genmachine));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public IdWorker (final long workerId) {
        if ( workerId > IdWorker.maxWorkerId || workerId < 0 ) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", IdWorker.maxWorkerId));
        }
        this.workerId = workerId;
    }
    public IdWorker() {
        // this.workerId = getAddress() % (IdWorker.maxWorkerId + 1);
        workerId = _genmachine % (IdWorker.maxWorkerId + 1);
    }

    public static long getId() {
        return worker.nextId();
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if ( lastTimestamp == timestamp ) {
            sequence = sequence + 1 & IdWorker.sequenceMask;
            if ( sequence == 0 ) {
                // System.out.println("###########" + sequenceMask);//等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        if ( timestamp < lastTimestamp ) {
            try {
                throw new Exception(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                        lastTimestamp - timestamp));
            } catch ( Exception e ) {
                logger.error(e.getMessage(), e);
            }
        }
        lastTimestamp = timestamp;
        return timestamp - twepoch << timestampLeftShift | workerId << IdWorker.workerIdShift | sequence;
    }

    private long tilNextMillis( final long lastTimestamp1 ) {
        long timestamp = timeGen();
        while ( timestamp <= lastTimestamp1 ) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
