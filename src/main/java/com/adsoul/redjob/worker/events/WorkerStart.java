package com.adsoul.redjob.worker.events;

import java.util.Objects;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

import com.adsoul.redjob.worker.Worker;

/**
 * New worker starts or resumes.
 */
public class WorkerStart extends ApplicationEvent implements WorkerEvent {
   /**
    * Worker.
    */
   private final Worker worker;

   /**
    * Constructor.
    *
    * @param worker
    *           Worker.
    */
   public WorkerStart(Worker worker) {
      super(worker);
      Assert.notNull(worker, "Precondition violated: worker != null.");
      this.worker = worker;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <W extends Worker> W getWorker() {
      return (W) worker;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof WorkerStart &&
            Objects.equals(worker, ((WorkerStart) o).worker);
   }

   @Override
   public int hashCode() {
      return Objects.hash(worker);
   }
}