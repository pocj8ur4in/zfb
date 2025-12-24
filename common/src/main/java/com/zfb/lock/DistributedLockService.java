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
   * Acquire distributed lock and execute task with default timeout
   *
   * @param lockKey
   * @param supplier Task to execute while holding the lock
   * @return Result of task execution
   * @throws LockAcquisitionException If lock acquisition fails after waiting
   */
  public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
    return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, supplier);
  }

  /**
   * Acquire distributed lock and execute task with custom timeout
   *
   * @param lockKey
   * @param waitTime Maximum seconds to wait for lock availability
   * @param leaseTime Maximum seconds to hold the lock before auto release
   * @param supplier Task to execute
   * @return Result of task execution
   * @throws LockAcquisitionException If lock acquisition fails after waiting
   */
  public <T> T executeWithLock(
      String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {

    String fullKey = LOCK_PREFIX + lockKey;
    RLock lock = redissonClient.getLock(fullKey);

    try {
      boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

      if (!acquired) {
        logger.error(
            "Failed to acquire distributed lock after {} seconds (key hash: {})",
            waitTime,
            sanitizeKeyForLogging(lockKey));
        throw new LockAcquisitionException(
            "Failed to acquire distributed lock after " + waitTime + " seconds");
      }

      logger.debug("Lock acquired: {}", fullKey);
      return supplier.get();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(
          "Thread interrupted while acquiring lock (key hash: {})",
          sanitizeKeyForLogging(lockKey),
          e);
      throw new LockAcquisitionException("Lock acquisition interrupted", e);
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
        logger.debug("Lock released: {}", fullKey);
      }
    }
  }

  /**
   * Try to execute task with lock without waiting. Returns default value if lock is not available.
   *
   * @param lockKey Lock identifier (must NOT contain sensitive information)
   * @param supplier Task to execute while holding the lock
   * @param defaultValue Default value to return if lock acquisition fails immediately
   * @return Result of task execution or default value if lock not available
   */
  public <T> T tryExecuteWithLock(String lockKey, Supplier<T> supplier, T defaultValue) {
    String fullKey = LOCK_PREFIX + lockKey;
    RLock lock = redissonClient.getLock(fullKey);

    try {
      boolean acquired = lock.tryLock(0, DEFAULT_LEASE_TIME, TimeUnit.SECONDS);

      if (!acquired) {
        logger.warn(
            "Lock not immediately available, returning default value (key hash: {})",
            sanitizeKeyForLogging(lockKey));
        return defaultValue;
      }

      logger.debug("Lock acquired: {}", fullKey);
      return supplier.get();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(
          "Thread interrupted while acquiring lock (key hash: {})",
          sanitizeKeyForLogging(lockKey),
          e);
      return defaultValue;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
        logger.debug("Lock released: {}", fullKey);
      }
    }
  }

  /**
   * Sanitize lock key for safe logging by creating a hash.
   *
   * <p>This prevents accidental exposure of sensitive information in logs while still providing a
   * unique identifier for debugging.
   *
   * @param lockKey Original lock key
   * @return Sanitized hash representation of the key
   */
  private String sanitizeKeyForLogging(String lockKey) {
    if (lockKey == null || lockKey.isEmpty()) {
      return "null";
    }
    // Use simple hash code for correlation in logs without exposing actual key
    return String.format("0x%08x", lockKey.hashCode());
  }
}
