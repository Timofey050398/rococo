package timofeyqa.kafka_log.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

@Configuration
public class RococoKafkaLogConsumerConfiguration {

  private final KafkaProperties kafkaProperties;

  @Autowired
  public RococoKafkaLogConsumerConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory(SslBundles sslBundles) {
    return new DefaultKafkaConsumerFactory<>(
        kafkaProperties.buildConsumerProperties(sslBundles),
        new StringDeserializer(),
        new StringDeserializer()
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(SslBundles sslBundles) {
    ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory
        = new ConcurrentKafkaListenerContainerFactory<>();
    concurrentKafkaListenerContainerFactory.setCommonErrorHandler(new DefaultErrorHandler());
    concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory(sslBundles));
    return concurrentKafkaListenerContainerFactory;
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
