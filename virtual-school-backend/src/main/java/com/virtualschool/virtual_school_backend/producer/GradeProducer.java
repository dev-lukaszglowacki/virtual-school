package com.virtualschool.virtual_school_backend.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GradeProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "notifications";

    @Autowired
    public GradeProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendGradeNotification(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
