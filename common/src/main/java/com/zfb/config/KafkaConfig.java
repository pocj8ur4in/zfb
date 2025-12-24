package com.zfb.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaConfig.KafkaProperties.class)
public class KafkaConfig {

  @Getter
  @Setter
  @ConfigurationProperties(prefix = "spring.kafka")
  public static class KafkaProperties {
    private List<String> bootstrapServers = new ArrayList<>();
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    @Getter
    @Setter
    public static class Consumer {
      private String groupId;
      private Map<String, Object> properties = new HashMap<>();
    }

    @Getter
    @Setter
    public static class Producer {
      private Map<String, Object> properties = new HashMap<>();
    }
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate(KafkaProperties kafkaProperties) {
    return new KafkaTemplate<>(producerFactory(kafkaProperties));
  }

  @Bean
  @SuppressWarnings("deprecation")
  public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = new HashMap<>();
    if (!kafkaProperties.getBootstrapServers().isEmpty()) {
      props.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
          String.join(",", kafkaProperties.getBootstrapServers()));
    }
    props.putAll(kafkaProperties.getProducer().getProperties());

    JsonSerializer<Object> valueSerializer = new JsonSerializer<>();

    return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), valueSerializer);
  }

  @Bean
  @SuppressWarnings("deprecation")
  public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = new HashMap<>();
    if (!kafkaProperties.getBootstrapServers().isEmpty()) {
      props.put(
          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
          String.join(",", kafkaProperties.getBootstrapServers()));
    }
    if (kafkaProperties.getConsumer().getGroupId() != null
        && !kafkaProperties.getConsumer().getGroupId().isBlank()) {
      props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
    }
    props.putAll(kafkaProperties.getConsumer().getProperties());

    JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>();
    valueDeserializer.addTrustedPackages("*");
    valueDeserializer.setUseTypeMapperForKey(false);

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
      KafkaProperties kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory(kafkaProperties));
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    return factory;
  }
}
