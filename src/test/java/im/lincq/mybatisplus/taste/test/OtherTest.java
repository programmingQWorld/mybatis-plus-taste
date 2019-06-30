package im.lincq.mybatisplus.taste.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OtherTest {



    public static void main(String[] args) throws InterruptedException {
        test();
    }
    public static void test () throws InterruptedException {
        //TableInfo table = TableInfoHelper.getTableInfo(User.class);
        //System.out.println(table);
        //simpleTestBlockingQueue();
        long startTime = System.currentTimeMillis();
        threadTestBlockingQueue();
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime) / 1e3 + "s");


    }


    /**
     * 多线程测试队列
     */
    static void threadTestBlockingQueue () throws InterruptedException {

        // 队列
        BlockingQueue queue = new BlockingQueue(20);

        List<Thread> threadList = new ArrayList<>(4);

        Runnable producerTask = () -> {
            // 入队
            for (int i = 0; i <= 19; i++) {
                try {
                    queue.put(i);
                    System.out.println("+++ "+ Thread.currentThread().getName() +" putting ele is ====== " + i + "\t" + queue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable consumerTask = () -> {
            // 出队
            for (int i = 0; i <= 19; i++) {
                try {
                    Object e = queue.take();
                    System.out.println("--------- "+ Thread.currentThread().getName() +" take ele is === " + (Integer)e + "\t" + queue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        // 两个生产者
        threadList.add(new Thread(producerTask, "p1"));
        threadList.add(new Thread(producerTask, "p2"));
        // 两个消费者
        threadList.add(new Thread(consumerTask, "c1"));
        threadList.add(new Thread(consumerTask, "c2"));

        threadList.forEach(Thread::start);
        for (Thread thread : threadList) {
            thread.join();
        }
    }

    /**
     * 简单测试队列
     * @throws InterruptedException
     */
    static void simpleTestBlockingQueue () throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(20);
        queue.put(2);
        queue.put(5);
        queue.put(6);
        queue.put(7);

        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
    }
}


class BlockingQueue {

    /** 显式锁 */
    private ReentrantLock lock = new ReentrantLock();

    /** 锁对应的条件变量 */
    private final Condition condition = lock.newCondition();

    /** 存放元素的数组 */
    private final Object[] items;

    /** 弹出元素的位置 */
    private int putIndex;

    /** 取出元素的位置 */
    private int takeIndex;

    /** 队列中元素的总数 */
    private int count;

    public BlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        // putIndex,takeIndex和count都会默认初始化为0
        this.items = new Object[capacity];
    }

    public void put(Object e) throws InterruptedException {
        synchronized (this) {
            while (this.count == items.length) {
                this.wait();
            }
            enqueue(e);
            this.notifyAll();
        }
    }

    public Object take() throws InterruptedException {
        synchronized (this) {
            while (this.count == 0) {
                this.wait();
            }
            Object e = dequeue();
            this.notifyAll();
            return e;
        }
    }

    private void enqueue (Object e) {
        this.items[putIndex] = e;
        count ++;
        putIndex++;
        if (putIndex == items.length) {
            putIndex = 0;
        }
    }

    private Object dequeue () {
        Object e = this.items[takeIndex];
        this.items[takeIndex] = null;
        count --;
        takeIndex ++;
        if (takeIndex == items.length) {
            takeIndex = 0;
        }
        return e;
    }

    @Override
    public String toString () {
        return "[count:" + this.count + ", putIndex:" + this.putIndex + ", takeIndex: " + this.takeIndex + ", items: [ " +
                Arrays.stream(items).reduce((a, b) -> a + ", " + b).orElse("") + "]"
                + "]";
    }
}