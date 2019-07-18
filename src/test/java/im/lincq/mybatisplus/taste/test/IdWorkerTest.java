package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.toolkit.IdWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>IdWorker并发测试</p>
 */
public class IdWorkerTest {

    /**
     * 测试
     */
    public static void main(String[] args) {
        int count = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            executorService.execute(new IdWorkerTest().new Task());
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Task implements Runnable {

        @Override
        public void run() {
            try {
                long id = IdWorker.getId();
                System.out.println(id);
            } catch (Exception e) {
                e.printStackTrace();;
            }
        }
    }

}
