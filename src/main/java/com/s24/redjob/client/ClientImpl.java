package com.s24.redjob.client;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.util.Assert;

import com.s24.redjob.lock.LockDao;
import com.s24.redjob.queue.QueueDao;

/**
 * Default implementation of {@link Client}.
 */
public class ClientImpl implements Client {
   /**
    * Queue dao.
    */
   private QueueDao queueDao;

   /**
    * Lock dao.
    */
   private LockDao lockDao;

   /**
    * Init.
    */
   @PostConstruct
   public void afterPropertiesSet() throws Exception {
      Assert.notNull(queueDao, "Precondition violated: queueDao != null.");
      Assert.notNull(lockDao, "Precondition violated: lockDao != null.");
   }

   @Override
   public long enqueue(String queue, Object job, boolean front) {
      return queueDao.enqueue(queue, job, front);
   }

   @Override
   public void dequeue(String queue, long id) {
      queueDao.dequeue(queue, id);
   }

   @Override
   public boolean tryLock(String lock, String holder, int timeout, TimeUnit unit) {
      return lockDao.tryLock(lock, holder, timeout, unit);
   }

   @Override
   public void releaseLock(String lock, String holder) {
      lockDao.releaseLock(lock, holder);
   }

   //
   // Injections.
   //

   /**
    * Queue dao.
    */
   public QueueDao getQueueDao() {
      return queueDao;
   }

   /**
    * Queue dao.
    */
   public void setQueueDao(QueueDao queueDao) {
      this.queueDao = queueDao;
   }

   /**
    * Lock dao.
    */
   public LockDao getLockDao() {
      return lockDao;
   }

   /**
    * Lock dao.
    */
   public void setLockDao(LockDao lockDao) {
      this.lockDao = lockDao;
   }
}