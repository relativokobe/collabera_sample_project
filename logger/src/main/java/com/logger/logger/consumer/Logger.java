package com.logger.logger.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Logger {

    @KafkaListener(topics = "Logs", groupId = "group_id")
    public void consume(String message){
        System.out.println("Message = " + message);
        //TODO Add to NoSQL
    }
}
