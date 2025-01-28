package br.com.ccs.threadlocal;

public class ThreadLocalExample {
    private static final ThreadLocal<Integer> threadLocalValue = ThreadLocal.withInitial(() -> 1);

    public static void main(String[] args) {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " - Valor inicial: " + threadLocalValue.get());

            threadLocalValue.set(threadLocalValue.get() + 1);
            System.out.println(threadName + " - Valor incrementado: " + threadLocalValue.get());
        };

        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");

        thread1.start();
        thread2.start();
    }
}
