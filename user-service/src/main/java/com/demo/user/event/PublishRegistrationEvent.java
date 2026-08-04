package com.demo.user.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublishRegistrationEvent {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void userNotification(String email)
    {
            kafkaTemplate.send("user-topic", email);
    }

}
