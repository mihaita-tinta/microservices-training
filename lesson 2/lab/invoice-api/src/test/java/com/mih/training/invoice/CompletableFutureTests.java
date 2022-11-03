package com.mih.training.invoice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompletableFutureTests {
    private static final Logger log = LoggerFactory.getLogger(CompletableFutureTests.class);

    @Test
    public void test1() {

        AtomicBoolean called = new AtomicBoolean();
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                log.debug("hello");
                called.set(true);
            }
        });

        //FIXME should be true
        assertTrue(called.get());
    }

    @Test
    public void test2() {

        AtomicBoolean called = new AtomicBoolean();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                log.debug("hello");
                called.set(true);
                return "something";

            }
        });

        //FIXME should be true
        assertTrue(called.get());
    }

    @Test
    public void test3() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "something");

        //FIXME check what methods from the future object can be called so the test passes.
        assertEquals("something else", future.get());
    }

    @Test
    public void test4() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            log.debug("start");
            return "start";
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
        }).thenApply(result -> {
            // FIXME can we run this code in another thread?
            return Thread.currentThread().getName();
        });

        assertTrue(res.get().startsWith("pool-1-thread"));
    }

    CountDownLatch latch = new CountDownLatch(2);

    CompletableFuture<String> getA(String input) {
        return CompletableFuture.supplyAsync(() -> {
            latch.countDown();
            return "a-" + input;
        });
    }

    CompletableFuture<Integer> getB(String a) {
        return CompletableFuture.supplyAsync(() -> {
            latch.countDown();
            return a.length();
        });
    }

    @Test
    public void test6() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = getA("start");

        // FIXME both getA("start") and getB("start") needs to be called at the same time
        // hint: use thenCombine

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
        assertEquals("a-start-5", future.get());
    }

    @Test
    public void test7() throws InterruptedException {

        int COUNTER = 10;
        CountDownLatch latch = new CountDownLatch(COUNTER);
        List<CompletableFuture<Integer>> completableFutureStream = IntStream.range(0, COUNTER)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    latch.countDown();
                    return i;
                }))
                .collect(Collectors.toList());

        // FIXME wait for all completable futures to finish and sum all the results
        // hint: use allOf

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    @Test
    public void test8() {

        CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> {
            log.info("a");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        });
        CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> {
            log.info("b");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 2;
        });

        // FIXME wait for any of the completable futures to finish
        // hint: use anyOf

        Object any = null;
        assertTrue(asList(1, 2).contains(any));
    }

    @Test
    public void test9() throws ExecutionException, InterruptedException {

        CompletableFuture.supplyAsync(() -> {
                    log.info("supplyAsync");
                    throw new IllegalStateException("ooops");
                }).thenApply(result -> {
                    log.info("thenApply");
                    return "b";
                }).thenAccept(result -> {
                    log.info("thenAccept - result {}", result);
                })
                // FIXME handle the exception
                .get();
    }

}
