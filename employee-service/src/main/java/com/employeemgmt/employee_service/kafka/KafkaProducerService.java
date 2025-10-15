package com.employeemgmt.employeeservice.kafka;

import com.employeemgmt.employeeservice.config.KafkaConfig;
import com.employeemgmt.employeeservice.event.EmployeeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, EmployeeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmployeeEvent(EmployeeEvent event) {
        logger.info("Sending employee event: {}", event.getEventType());

        CompletableFuture<SendResult<String, EmployeeEvent>> future =
                kafkaTemplate.send(KafkaConfig.EMPLOYEE_EVENTS_TOPIC, event.getEmployeeIdCode(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Successfully sent employee event: {} with offset: {}",
                        event.getEventType(), result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to send employee event: {}", event.getEventType(), ex);
            }
        });
    }
}