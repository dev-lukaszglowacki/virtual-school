package com.virtualschool.reportingservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReportingConsumer {

    @KafkaListener(topics = "reporting_jobs", groupId = "reporting-group")
    public void listen(String message) {
        System.out.println("Received message in reporting-group: " + message);
    }
}
