package org.example.budgeting.infrastructure.messaging.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.budgeting.infrastructure.messaging.events.MaterialDefined;
import org.example.budgeting.infrastructure.messaging.events.MaterialDeleted;
import org.example.budgeting.infrastructure.messaging.events.ProductDefined;
import org.example.budgeting.infrastructure.messaging.events.ProductDeleted;
import org.example.budgeting.infrastructure.messaging.events.RecipeDefined;
import org.example.budgeting.infrastructure.messaging.events.RecipeDeleted;
import org.example.budgeting.infrastructure.messaging.events.ReservationProcessed;
import org.example.budgeting.infrastructure.messaging.events.StockLevelChanged;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MaterialDefined> materialDefinedContainerFactory() {
        return containerFactory(MaterialDefined.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MaterialDeleted> materialDeletedContainerFactory() {
        return containerFactory(MaterialDeleted.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecipeDefined> recipeDefinedContainerFactory() {
        return containerFactory(RecipeDefined.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecipeDeleted> recipeDeletedContainerFactory() {
        return containerFactory(RecipeDeleted.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductDefined> productDefinedContainerFactory() {
        return containerFactory(ProductDefined.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductDeleted> productDeletedContainerFactory() {
        return containerFactory(ProductDeleted.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockLevelChanged> stockLevelChangedContainerFactory() {
        return containerFactory(StockLevelChanged.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationProcessed> reservationProcessedContainerFactory() {
        return containerFactory(ReservationProcessed.class);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> containerFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        return factory;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(targetType));
    }
}
