//package com.kii.beehive.portal.helper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicLong;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.beehive.portal.store.TestInit;
//
///**
// * Created by USER on 12/27/15.
// */
//public class TestAuthInfoCacheService extends TestInit {
//
//    @Autowired
//    private AuthInfoCacheService authInfoCacheService;
//
//    final int SAVE_COUNT = 1000;
//
//    final int QUERY_COUNT = 1000;
//
//    @Test
//    public void testPerformance() {
//
//        Runtime.getRuntime().gc();
//        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//
//        // save
//        long saveTime = 0;
//
//        for(int i =0;i<SAVE_COUNT;i++) {
//            long start = System.currentTimeMillis();
//            authInfoCacheService.saveToken("user_id_" + i, "long_long_long_token_" + i);
//            long end = System.currentTimeMillis();
//
//            saveTime += (end - start);
//
//            System.out.println("token num:" + i + " saved");
//        }
//
//        // query
//        long queryTime = 0;
//
//        for(int i=0;i<QUERY_COUNT;i++) {
//            long start = System.currentTimeMillis();
//            authInfoCacheService.getAuthInfo("long_long_long_token_" + i);
//            long end = System.currentTimeMillis();
//
//            queryTime += (end - start);
//            System.out.println("query count:" + i);
//        }
//
//        Runtime.getRuntime().gc();
//        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//
//        System.out.println("save total count:" + SAVE_COUNT + " save average time:" + (saveTime / SAVE_COUNT));
//        System.out.println("query total count:" + QUERY_COUNT + " query average time:" + (queryTime / QUERY_COUNT));
//        System.out.println("used memory:" + (endMemory - startMemory));
//
//    }
//
//    @Test
//    public void testPerformanceInMultiThreads() throws Exception {
//
//        AtomicLong saveLock = new AtomicLong(0);
//
//        Runtime.getRuntime().gc();
//        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//
//        // save
//        long saveTime = 0;
//
//        List<SaveThread> saveThreadList = new ArrayList<>();
//
//        for(int i = 0; i< SAVE_COUNT; i++) {
//            saveThreadList.add(new SaveThread(i, saveLock));
//        }
//        synchronized (saveLock) {
//            for(int i = 0; i< SAVE_COUNT; i++) {
//                saveThreadList.get(i).start();
//            }
//
//            System.out.println("main thread wait");
//            saveLock.wait();
//        }
//
//        for(int i=0;i<SAVE_COUNT;i++) {
//            saveTime += saveThreadList.get(i).getExecuteTime();
//        }
//
//        // query
//        AtomicLong queryLock = new AtomicLong(0);
//
//        long queryTime = 0;
//
//        List<QueryThread> queryThreadList = new ArrayList<>();
//        for(int i = 0; i< QUERY_COUNT; i++) {
//            queryThreadList.add(new QueryThread(i, queryLock));
//        }
//
//        synchronized (queryLock) {
//            for(int i = 0; i< QUERY_COUNT; i++) {
//                queryThreadList.get(i).start();
//            }
//
//            queryLock.wait();
//        }
//
//        for(int i=0;i<QUERY_COUNT;i++) {
//            queryTime += queryThreadList.get(i).getExecuteTime();
//        }
//
//        Runtime.getRuntime().gc();
//        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//
//
//        System.out.println("save total count:" + SAVE_COUNT + " save average time:" + (saveTime / SAVE_COUNT));
//        System.out.println("query total count:" + QUERY_COUNT + " query average time:" + (queryTime / QUERY_COUNT));
//        System.out.println("used memory:" + (endMemory - startMemory));
//
//    }
//
//    class SaveThread extends Thread {
//
//        private long index = -1;
//
//        private long executeTime = -1;
//
//        private AtomicLong lock;
//
//        public SaveThread(long index, AtomicLong lock) {
//            this.index = index;
//            this.lock = lock;
//        }
//
//        @Override
//        public void run() {
//
//            long start = System.currentTimeMillis();
//            authInfoCacheService.saveToken("user_id_" + index, "long_long_long_token_" + index);
//            long end = System.currentTimeMillis();
//            executeTime += (end - start);
//
//            System.out.println("token num:" + index + " saved");
//
//            synchronized (lock) {
//                lock.incrementAndGet();
//                if(lock.longValue() == SAVE_COUNT) {
//                    lock.notify();
//                }
//            }
//        }
//
//        public long getExecuteTime() {
//            return this.executeTime;
//        }
//    }
//
//    class QueryThread extends Thread {
//
//        private long index = -1;
//
//        private long executeTime = -1;
//
//        private AtomicLong lock;
//
//        public QueryThread(long index, AtomicLong lock) {
//            this.index = index;
//            this.lock = lock;
//        }
//
//        @Override
//        public void run() {
//
//            long start = System.currentTimeMillis();
//            authInfoCacheService.getAuthInfo("long_long_long_token_" + index);
//            long end = System.currentTimeMillis();
//            executeTime += (end - start);
//
//            System.out.println("query count:" + index);
//
//            synchronized (lock) {
//                lock.incrementAndGet();
//
//                if(lock.longValue() == QUERY_COUNT) {
//                    lock.notify();
//                }
//            }
//        }
//
//        public long getExecuteTime() {
//            return this.executeTime;
//        }
//    }
//
//}