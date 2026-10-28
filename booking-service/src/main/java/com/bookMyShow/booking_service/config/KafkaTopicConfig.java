package com.bookMyShow.booking_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookingInitiateTopic() {
        return TopicBuilder.name("booking-initiate")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingConfirmedTopic() {
        return TopicBuilder.name("booking-confirmed")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingCancelledTopic() {
        return TopicBuilder.name("booking-cancelled")
                .partitions(10)
                .replicas(1)
                .build();
    }
}
