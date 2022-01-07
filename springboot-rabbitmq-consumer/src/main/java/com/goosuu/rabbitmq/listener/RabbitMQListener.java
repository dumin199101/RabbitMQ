package com.goosuu.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {
    @RabbitListener(queues = "item_queue")
    public void RecvListener(String message){
        System.out.println(message);
    }
}
