package im.lincq.mybatisplus.taste.toolkit;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * <p>并发测试IdWorker的工作情况</p>
 */
public class ContiPerfTest {

    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    @Test
    @PerfTest(invocations = 20000000, threads = 16)
    public void testIdWorker() throws Exception {
        IdWorker.getId();
    }
}
