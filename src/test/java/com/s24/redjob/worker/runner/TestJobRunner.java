package com.s24.redjob.worker.runner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

/**
 * Job runner for {@link TestJob}s.
 */
public class TestJobRunner implements Runnable {
   /**
    * If the job's value if {@value #EXCEPTION_VALUE}, this runner throws {@link #EXCEPTION} when processing it.
    */
   public static final String EXCEPTION_VALUE = "exception";

   /**
    * The exception that will be thrown.
    */
   public static final Error EXCEPTION = new Error(EXCEPTION_VALUE);

   /**
    * Last executed job.
    */
   private static volatile TestJob lastJob;

   /**
    * Job.
    */
   private TestJob job;

   /**
    * Latch.
    */
   private static volatile CountDownLatch latch = new CountDownLatch(0);

   /**
    * Reset latch to the given value.
    */
   public static void resetLatch(int count) {
      latch = new CountDownLatch(count);
   }

   /**
    * Constructor for creating a runner that executed a job.
    *
    * @param job
    *           Job.
    */
   public TestJobRunner(TestJob job) {
      Assert.notNull(job, "Precondition violated: job != null.");

      this.job = job;
   }

   @Override
   public void run() {
      TestJobRunner.lastJob = job;

      try {
         // Simulate execution of job...
         Thread.sleep(100);
      } catch (InterruptedException e) {
         // ignore
      }

      latch.countDown();
      if (EXCEPTION_VALUE.equals(job.getValue())) {
         throw EXCEPTION;
      }
   }

   /**
    * Await latch to be counted down.
    *
    * @return Latch has been counted down to zero.
    */
   public static boolean awaitLatch(long timeout, TimeUnit unit) throws InterruptedException {
      return latch.await(timeout, unit);
   }

   /**
    * Job.
    */
   public static TestJob getLastJob() {
      return lastJob;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof TestJobRunner && job.equals(((TestJobRunner) o).job);
   }

   @Override
   public int hashCode() {
      return job.hashCode();
   }
}
