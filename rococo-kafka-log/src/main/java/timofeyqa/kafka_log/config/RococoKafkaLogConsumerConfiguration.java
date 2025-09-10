package timofeyqa.kafka_log.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import timofeyqa.kafka_log.model.LogJson;

@Configuration
public class RococoKafkaLogConsumerConfiguration {

  private final KafkaProperties kafkaProperties;

  @Autowired
  public RococoKafkaLogConsumerConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  @Bean
  public ConsumerFactory<String, LogJson> consumerFactory(SslBundles sslBundles) {
    final JsonDeserializer<LogJson> jsonDeserializer = new JsonDeserializer<>(LogJson.class);
    jsonDeserializer.addTrustedPackages("*");
    return new DefaultKafkaConsumerFactory<>(
        kafkaProperties.buildConsumerProperties(sslBundles),
        new StringDeserializer(),
        jsonDeserializer
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, LogJson> kafkaListenerContainerFactory(SslBundles sslBundles) {
    ConcurrentKafkaListenerContainerFactory<String, LogJson> concurrentKafkaListenerContainerFactory
        = new ConcurrentKafkaListenerContainerFactory<>();
    concurrentKafkaListenerContainerFactory.setCommonErrorHandler(new DefaultErrorHandler());
    concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory(sslBundles));
    return concurrentKafkaListenerContainerFactory;
  }
}
