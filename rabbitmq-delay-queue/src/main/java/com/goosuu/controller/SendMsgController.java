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

     @GetMapping("sendExpireMsg/{message}/{ttlTime}")
     public void sendMsg(@PathVariable String message,@PathVariable String ttlTime){
          log.info("当前时间:{},给队列发送了一条延时{}毫秒的消息:{}",new Date(),ttlTime,message);
          rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE,RabbitMQConfig.C_ROUTING_KEY,message,correlationData -> {
               correlationData.getMessageProperties().setExpiration(ttlTime);
               return correlationData;
          });
     }

     @GetMapping("sendDelayMsg/{message}/{delayTime}")
     public void sendMsg(@PathVariable String message,@PathVariable Integer delayTime){
          log.info("当前时间:{},给队列发送了一条延时{}毫秒的消息:{}",new Date(),delayTime,message);
          rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE_NAME,RabbitMQConfig.DELAYED_ROUTING_KEY,message,correlationData -> {
               correlationData.getMessageProperties().setDelay(delayTime);
               return correlationData;
          });
     }


}

