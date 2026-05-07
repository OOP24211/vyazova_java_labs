package main.java.concurrent.manualpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadBlockingQueue {
    private final BlockingQueue<Runnable> queue =
            new LinkedBlockingQueue<>();

    private final List<Thread> workersCount =
            new ArrayList<>();

    public ThreadBlockingQueue(int workers) {
        for (int i = 0; i < workers; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Runnable task = queue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });

            workersCount.add(thread);
            thread.start();
        }
    }
    public void submit(Runnable task) {
        queue.add(task);
    }

    public void shutdown() {
        for (Thread worker : workersCount) {
            worker.interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadBlockingQueue pool = new ThreadBlockingQueue(3);

        for (int i = 0; i < 10; i++) {
            int id = i;

            pool.submit(() -> {
                long sum = 0;
                for (int j = 0; j < 10_000_000; j++) {
                    sum += j;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(
                        "task " + id + "made by " + Thread.currentThread().getName()
                );
            });
        }
        Thread.sleep(5000);
        pool.shutdown();
    }
}