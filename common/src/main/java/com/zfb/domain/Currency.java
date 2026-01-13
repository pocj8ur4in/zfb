package com.zfb.domain;

import lombok.Getter;

@Getter
public enum Currency {
  KRW("KRW"),
  USD("USD"),
  EUR("EUR"),
  JPY("JPY"),
  GBP("GBP"),
  CAD("CAD"),
  AUD("AUD"),
  NZD("NZD"),
  SGD("SGD"),
  HKD("HKD"),
  CNY("CNY"),
  CHF("CHF"),
  MYR("MYR"),
  PHP("PHP"),
  IDR("IDR"),
  THB("THB"),
  VND("VND"),
  TWD("TWD");

  private final String code;

  Currency(String code) {
    this.code = code;
  }
}
