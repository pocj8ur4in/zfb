package com.zfb.forex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zfb"})
public class ForexApplication {

  public static void main(String[] args) {
    SpringApplication.run(ForexApplication.class, args);
  }
}
