package com.employeemgmt.employeeservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String EMPLOYEE_EVENTS_TOPIC = "employee-events";
    public static final String DEPARTMENT_EVENTS_TOPIC = "department-events";

    @Bean
    public NewTopic employeeEventsTopic() {
        return TopicBuilder
                .name(EMPLOYEE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic departmentEventsTopic() {
        return TopicBuilder
                .name(DEPARTMENT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
