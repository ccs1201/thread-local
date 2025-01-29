package br.com.ccs.threadlocal;

import java.lang.reflect.Field;

public class ThreadLocalGCExample {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        threadLocal.set("Um objeto qualquer"); // 10 MB
        inspectThreadLocalMap(Thread.currentThread());
        var tlAttr = threadLocal.get();

        //remove o objeto do contexto da thread main
        threadLocal.remove();

        Thread thread = new Thread(() -> {
            System.out.println("### Iniciando outra thread");
            threadLocal.set(tlAttr);
            System.out.println("Valor setado na outra thread");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setName("outra thread");

        thread.start();
        Thread.sleep(2000);
        inspectThreadLocalMap(thread);
        thread.join(); // Aguarda o término da thread

        inspectThreadLocalMap(thread);
        Thread.sleep(2000);

        //Aqui não deve imprimir o valor 'Um objeto qualquer'
        inspectThreadLocalMap(Thread.currentThread());
    }

    private static void inspectThreadLocalMap(Thread thread) throws Exception {
        System.out.println("### Inspecionando ThreadLocalMap \nNome da Thread: " + thread.getName());
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
