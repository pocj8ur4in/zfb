package com.zfb.config;

import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig.SingleBaggageField;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

  @Bean
  public Propagation.Factory propagationFactory() {
    return BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
        .add(SingleBaggageField.remote(BaggageField.create("userId")))
        .add(SingleBaggageField.remote(BaggageField.create("traceId")))
        .build();
  }
}
