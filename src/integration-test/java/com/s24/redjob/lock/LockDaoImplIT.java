package com.s24.redjob.lock;

import com.s24.redjob.TestRedis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration test for {@link LockDaoImpl}.
 */
class LockDaoImplIT {
   /**
    * DAO under test.
    */
   private LockDaoImpl dao = new LockDaoImpl();

   /**
    * Redis access.
    */
   private StringRedisTemplate redis;

   @BeforeEach
   void setUp() {
      RedisConnectionFactory connectionFactory = TestRedis.connectionFactory();

      dao.setConnectionFactory(connectionFactory);
      dao.setNamespace("namespace");
      dao.afterPropertiesSet();

      redis = new StringRedisTemplate();
      redis.setConnectionFactory(connectionFactory);
      redis.afterPropertiesSet();
   }

   @Test
   void tryLock() {
      String key = "namespace:lock:test";

      assertTrue(dao.tryLock("test", "holder", 10, TimeUnit.SECONDS));

      assertEquals("holder", redis.opsForValue().get(key));
      assertTrue(redis.getExpire(key, TimeUnit.MILLISECONDS) > 9000);
      assertTrue(redis.getExpire(key, TimeUnit.MILLISECONDS) <= 10000);

      assertFalse(dao.tryLock("test", "someoneelse", 10, TimeUnit.SECONDS));
   }

   @Test
   void tryLock_parallel() throws Exception {
      final int threads = 100;

      CompletableFuture<Void> lock = new CompletableFuture<>();
      AtomicInteger acquired = new AtomicInteger(0);
      AtomicInteger notAcquired = new AtomicInteger(0);

      ExecutorService pool = Executors.newFixedThreadPool(threads);
      for (int i = 0; i < threads; i++) {
         String holder = Integer.toString(i);
         pool.submit(() -> {
            try {
               // Warmup redis connection pool.
               redis.hasKey("dummy");

               // Wait for start.
               lock.get();

               // Try to acquire lock and log success.
               if (dao.tryLock("test", holder, 10, TimeUnit.SECONDS)) {
                  acquired.incrementAndGet();
               } else {
                  notAcquired.incrementAndGet();
               }

            } catch (Exception e) {
               fail("No exception in test threads expected.");
               System.out.println("failed");
            }
         });
      }

      // Wait at max 10 seconds for all threads to arrive at start lock.
      for (int i = 0; i < 100 && lock.getNumberOfDependents() < threads; i++) {
         Thread.sleep(100);
      }
      assertEquals(threads, lock.getNumberOfDependents());
      System.out.println("All locks started.");

      // Start all threads at once.
      lock.complete(null);

      // Wait at max 10 seconds for all threads to try to acquire lock.
      for (int i = 0; i < 100 && acquired.get() + notAcquired.get() < threads; i++) {
         Thread.sleep(100);
         System.out.println(acquired.get() + " locks acquired.");
         System.out.println(notAcquired.get() + " locks not acquired.");
      }

      // Check that exactly one thread was able to acquire the lock.
      assertEquals(1, acquired.get());
      assertEquals(threads - 1, notAcquired.get());
   }

   @Test
   void releaseLock() {
      String key = "namespace:lock:test";

      assertTrue(dao.tryLock("test", "holder", 10, TimeUnit.SECONDS));

      dao.releaseLock("test", "holder");
      assertFalse(redis.hasKey(key));

      // After releasing the lock someone else should be able to acquire the lock.
      assertTrue(dao.tryLock("test", "someoneelse", 10, TimeUnit.SECONDS));
   }
}
