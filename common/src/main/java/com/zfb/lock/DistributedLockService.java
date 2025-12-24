package com.zfb.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedLockService {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(DistributedLockService.class);

  private static final String LOCK_PREFIX = "zfb:lock:";
  private static final long DEFAULT_WAIT_TIME = 5L;
  private static final long DEFAULT_LEASE_TIME = 10L;

  private final RedissonClient redissonClient;

  /**
   * Acquire distributed lock and execute task
   *
   * @param lockKey
   * @param supplier Task to execute
   * @return Result of task execution
   * @throws RuntimeException If lock acquisition fails
   */
  public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
    return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, supplier);
  }

  /**
   * Acquire distributed lock and execute task with timeout
   *
   * @param lockKey
   * @param waitTime Seconds to wait for lock available
   * @param leaseTime Seconds to hold the lock
   * @param supplier Task to execute
   * @return Result of task execution
   * @throws RuntimeException If lock acquisition fails
   */
  public <T> T executeWithLock(
      String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {

    String fullKey = LOCK_PREFIX + lockKey;
    RLock lock = redissonClient.getLock(fullKey);

    try {
      boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

      if (!acquired) {
        logger.error("Failed to acquire lock: {}", fullKey);
        throw new RuntimeException("Failed to acquire distributed lock: " + lockKey);
      }

      logger.debug("Lock acquired: {}", fullKey);
      return supplier.get();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Thread interrupted while acquiring lock: {}", fullKey, e);
      throw new RuntimeException("Lock acquisition interrupted", e);
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
        logger.debug("Lock released: {}", fullKey);
      }
    }
  }

  /**
   * Execute task without lock
   *
   * @param lockKey
   * @param supplier Task to execute
   * @param defaultValue Default value to return if lock acquisition fails
   * @return Result of task execution or default value
   */
  public <T> T tryExecuteWithLock(String lockKey, Supplier<T> supplier, T defaultValue) {
    String fullKey = LOCK_PREFIX + lockKey;
    RLock lock = redissonClient.getLock(fullKey);

    try {
      boolean acquired = lock.tryLock(0, DEFAULT_LEASE_TIME, TimeUnit.SECONDS);

      if (!acquired) {
        logger.warn("Lock not acquired, returning default value: {}", fullKey);
        return defaultValue;
      }

      logger.debug("Lock acquired: {}", fullKey);
      return supplier.get();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Thread interrupted while acquiring lock: {}", fullKey, e);
      return defaultValue;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
        logger.debug("Lock released: {}", fullKey);
      }
    }
  }
}
