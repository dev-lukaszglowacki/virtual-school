package com.virtualschool.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void listen(String message) {
        System.out.println("Received message in notification-group: " + message);
    }
}
