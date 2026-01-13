package com.zfb.forex.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Order(0)
@Component
public class DataSourceAspect {

  @Around("@annotation(transactional)")
  public Object determineDataSource(ProceedingJoinPoint joinPoint, Transactional transactional)
      throws Throwable {

    try {
      if (transactional.readOnly()) {
        DataSourceContextHolder.setDataSourceType(DataSourceType.REPLICA);
      } else {
        DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
      }

      return joinPoint.proceed();

    } finally {
      DataSourceContextHolder.removeDataSourceType();
    }
  }
}
