package br.com.ccs.threadlocal;

public class InheritableThreadLocalExample {
    private static final InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) {
        // Configura o valor inicial no thread principal
        inheritableThreadLocal.set("Valor no thread-pai");

        Thread threadFilha = new Thread(() -> {
            // A thread-filha herda o valor do thread-pai
            System.out.println("Thread-filha: " + inheritableThreadLocal.get());
        });

        threadFilha.start();

        System.out.println("Thread-pai: " + inheritableThreadLocal.get());
    }
}
