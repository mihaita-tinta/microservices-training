package com.mih.training.invoice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class CompletableFutureTests {
    private static final Logger log = getLogger(CompletableFutureTests.class);

    @Test
    public void test1() throws ExecutionException, InterruptedException {

        AtomicBoolean called = new AtomicBoolean();
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                log.debug("start");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                log.debug("hello");
                called.set(true);
            }
        });
        log.debug("waiting?");

        future.get();
        //FIXME should be true
        assertTrue(called.get());
    }

    public CompletableFuture<String> getString() {
        return CompletableFuture.completedFuture("");
    }


    @Test
    public void test2() throws ExecutionException, InterruptedException {

        AtomicBoolean called = new AtomicBoolean();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            log.debug("hello");
            called.set(true);
            return "something";

        });

        String s = future.get();
        //FIXME should be true
        assertTrue(called.get());
    }
    @Test
    public void test3() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "something");

        CompletableFuture<String> stringCompletableFuture = future.thenApply(s -> {
            return s + " else";
        });
        //FIXME check what methods from the future object can be called so the test passes.
        assertEquals("something else", stringCompletableFuture.get());
    }

    @Test
    public void test4() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.debug("start");
            return "start";
        });
        CompletableFuture<String> voidCompletableFuture = future.thenCompose(s -> {
            return CompletableFuture.supplyAsync(() -> {

                latch.countDown();
                return "";
            });
        });
        latch.await(1, TimeUnit.SECONDS);
        //FIXME on the future, after the first completionStage call a method to invoke latch.countDown();.
        // What difference is between thenAccept and thenRun?
        assertEquals(0, latch.getCount());
    }

    @Test
    public void test5() throws ExecutionException, InterruptedException {

        Executor executor = Executors.newFixedThreadPool(1);
        CompletableFuture<String> res = CompletableFuture.supplyAsync(() -> {
            return "start";
        }).thenApplyAsync(result -> {
            // FIXME can we run this code in another thread?
            return Thread.currentThread().getName();
        }, executor);

        assertTrue(res.get().startsWith("pool-1-thread"));
    }
}
