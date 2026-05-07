package main.java.concurrent.executorservice;

import java.util.concurrent.*;

public class ExecutorServiceThread {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(3);

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

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
