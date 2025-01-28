package br.com.ccs.threadlocal;

import java.lang.reflect.Field;

public class ThreadLocalGCExample {
    private static final ThreadLocal<byte[]> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(() -> {
            threadLocal.set(new byte[10 * 1024 * 1024]); // 10 MB
            System.out.println("Valor setado");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
        Thread.sleep(1000);
        inspectThreadLocalMap(thread);
        thread.join(); // Aguarda o término da thread

        // O mapa da thread ainda mantém a referência ao valor
        //System.gc(); // Força o garbage collector
        System.out.println("GC forçado. Verifique vazamentos.");
        inspectThreadLocalMap(thread);
        Thread.sleep(2000);

    }

    private static void inspectThreadLocalMap(Thread thread) throws Exception {
        Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
        threadLocalsField.setAccessible(true);

        Object threadLocalMap = threadLocalsField.get(thread);
        if (threadLocalMap != null) {
            Field tableField = threadLocalMap.getClass().getDeclaredField("table");
            tableField.setAccessible(true);

            Object[] table = (Object[]) tableField.get(threadLocalMap);
            for (Object entry : table) {
                if (entry != null) {
                    Field valueField = entry.getClass().getDeclaredField("value");
                    valueField.setAccessible(true);
                    Object value = valueField.get(entry);

                    System.out.println("Valor encontrado: " + value);
                }
            }
        } else {
            System.out.println("ThreadLocalMap null.");
        }
    }
}
