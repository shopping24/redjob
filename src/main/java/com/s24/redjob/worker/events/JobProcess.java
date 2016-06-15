package com.s24.redjob.worker.events;

import java.util.Objects;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

import com.s24.redjob.worker.Worker;

/**
 * Worker processes a job.
 */
public class JobProcess extends ApplicationEvent {
   /**
    * Worker.
    */
   private final Worker worker;

   /**
    * Queue.
    */
   private final String queue;

   /**
    * Job.
    */
   private final Object job;

   /**
    * Veto against job execution?.
    */
   private boolean veto = false;

   /**
    * Constructor.
    *
    * @param worker
    *           Worker.
    * @param queue
    *           Queue.
    * @param job
    *           Job.
    */
   public JobProcess(Worker worker, String queue, Object job) {
      super(worker);
      Assert.notNull(worker, "Precondition violated: worker != null.");
      Assert.hasLength(queue, "Precondition violated: queue has length.");
      Assert.notNull(job, "Precondition violated: job != null.");
      this.worker = worker;
      this.queue = queue;
      this.job = job;
   }

   /**
    * Worker.
    */
   public <W extends Worker> W getWorker() {
      return (W) worker;
   }

   /**
    * Queue.
    */
   public String getQueue() {
      return queue;
   }

   /**
    * Job.
    */
   public <J> J getJob() {
      return (J) job;
   }

   /**
    * Veto against execution of the job.
    */
   public void veto() {
      this.veto = true;
   }

   /**
    * Has been vetoed against execution of the job?.
    */
   public boolean isVeto() {
      return veto;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof JobProcess &&
            Objects.equals(worker, ((JobProcess) o).worker) &&
            Objects.equals(queue, ((JobProcess) o).queue) &&
            Objects.equals(job, ((JobProcess) o).job);
   }

   @Override
   public int hashCode() {
      return Objects.hash(worker, queue, job);
   }
}
