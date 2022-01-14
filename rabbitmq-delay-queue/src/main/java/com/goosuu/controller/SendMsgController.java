package com.goosuu.controller;

import com.goosuu.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("ttl")
public class SendMsgController {

     @Autowired
     private RabbitTemplate rabbitTemplate;

     @GetMapping("sendMsg/{message}")
     public void sendMsg(@PathVariable String message){
          log.info("当前时间:{},给两个ttl队列发送了一条消息:{}",new Date(),message);
          rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE,RabbitMQConfig.A_ROUTING_KEY,"消息来自ttl为10s的队列" + message);
          rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE,RabbitMQConfig.B_ROUTING_KEY,"消息来自ttl为40s的队列" + message);
     }


}

