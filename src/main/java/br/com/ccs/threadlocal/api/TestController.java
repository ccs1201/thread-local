package br.com.ccs.threadlocal.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping
    public String test() {
        Thread.currentThread().setName("Thread Principal");
        var attr = RequestContextHolder.getRequestAttributes();
        CompletableFuture.runAsync(() -> {
            try {
                Thread.currentThread().setName("Thread completableFuture");
                RequestContextHolder.setRequestAttributes(attr, false);

                printThreadName();
                printHeaders();

                printRequestAttributes(RequestContextHolder.getRequestAttributes());
                printSessionAttributes(RequestContextHolder.getRequestAttributes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }).join();

        printThreadName();

        printRequestAttributes(RequestContextHolder.getRequestAttributes());
        printSessionAttributes(RequestContextHolder.getRequestAttributes());
        return "Teste";
    }

    private static void printThreadName() {
        System.out.println(Thread.currentThread().getName() + "\n");
    }

    private void printRequestAttributes(RequestAttributes requestAttributes) {

        System.out.println("### Request Attributes\n");
        for (var entry : requestAttributes.getAttributeNames(RequestAttributes.SCOPE_REQUEST)) {
            System.out.println(entry + " : " + requestAttributes
                    .getAttribute(entry, RequestAttributes.SCOPE_REQUEST).getClass().getSimpleName());
        }
    }

    private void printSessionAttributes(RequestAttributes requestAttributes) {
        System.out.println("### Session Attributes\n");
        for (var entry : requestAttributes.getAttributeNames(RequestAttributes.SCOPE_SESSION)) {
            System.out.println(entry + " : " + requestAttributes
                    .getAttribute(entry, RequestAttributes.SCOPE_SESSION).getClass().getSimpleName());
        }
    }

    private void printHeaders() {
        System.out.println("### Headers\n");

        var servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        servletRequestAttributes
                .getRequest()
                .getHeaderNames()
                .asIterator()
                .forEachRemaining(headerName ->
                        System.out.println(headerName + " : " + servletRequestAttributes.getRequest().getHeader(headerName)));
        System.out.println();
    }
}

