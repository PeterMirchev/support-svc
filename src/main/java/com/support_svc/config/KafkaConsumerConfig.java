package com.support_svc.config;

import com.support_svc.event.dto.CaseMessageRequest;
import com.support_svc.event.dto.CaseUpdateRequest;
import com.support_svc.event.dto.SupportCaseEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Profile("!test")
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private Map<String, Object> consumerConfigs() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }


    @Bean
    public ConsumerFactory<String, SupportCaseEvent> supportCaseConsumerFactory() {

        JsonDeserializer<SupportCaseEvent> deserializer = new JsonDeserializer<>(SupportCaseEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SupportCaseEvent> supportCaseKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, SupportCaseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(supportCaseConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CaseUpdateRequest> caseUpdateConsumerFactory() {

        JsonDeserializer<CaseUpdateRequest> deserializer = new JsonDeserializer<>(CaseUpdateRequest.class);
        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CaseUpdateRequest> caseUpdateKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CaseUpdateRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(caseUpdateConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CaseMessageRequest> caseMessageConsumerFactory() {

        JsonDeserializer<CaseMessageRequest> deserializer = new JsonDeserializer<>(CaseMessageRequest.class);
        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CaseMessageRequest> caseMessageKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CaseMessageRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(caseMessageConsumerFactory());
        return factory;
    }
}
