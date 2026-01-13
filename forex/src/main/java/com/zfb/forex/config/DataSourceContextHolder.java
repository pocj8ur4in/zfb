package com.zfb.forex.config;

import org.springframework.util.Assert;

public class DataSourceContextHolder {

  private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

  public static void setDataSourceType(DataSourceType dataSourceType) {
    Assert.notNull(dataSourceType, "dataSourceType cannot be null");
    contextHolder.set(dataSourceType);
  }

  public static DataSourceType getDataSourceType() {
    return contextHolder.get();
  }

  public static void removeDataSourceType() {
    contextHolder.remove();
  }
}
