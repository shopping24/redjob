package com.s24.redjob.queue;

import com.s24.redjob.worker.Execution;

/**
 * DAO for accessing job queues.
 */
public interface QueueDao {
   /**
    * Enqueue the given job to the given queue.
    *
    * @param queue
    *           Queue name.
    * @param job
    *           Job.
    * @param front
    *           Enqueue job at front of the queue, so that the job is the first to be executed?.
    * @return Id assigned to the job.
    */
   Execution enqueue(String queue, Object job, boolean front);

   /**
    * Dequeue the job with the given id from the given queue.
    *
    * @param queue
    *           Queue name.
    * @param id
    *           Id of the job.
    * @return true, if at least one job is dequeued.
    */
   boolean dequeue(String queue, long id);

   /**
    * Get a job from the given queue.
    *
    * @param queue
    *           Queue name.
    * @param id
    *           Id of the job.
    * @return execution, or null if not existing.
    */
   Execution peek(String queue, long id);

   /**
    * Pop first job from queue.
    *
    * @param queue
    *           Queue name.
    * @param worker
    *           Name of worker.
    * @return Job or null, if none is in the queue.
    */
   Execution pop(String queue, String worker);

   /**
    * Remove job from inflight queue.
    *
    * @param queue
    *           Queue name.
    * @param worker
    *           Name of worker.
    */
   void removeInflight(String queue, String worker);

   /**
    * Restore job from inflight queue.
    *
    * @param queue
    *           Queue name.
    * @param worker
    *           Name of worker.
    */
   void restoreInflight(String queue, String worker);
}
