package com.goosuu;

import com.goosuu.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class DeadLetterConsumer {
    @RabbitListener(queues = RabbitMQConfig.DEAD_QUEUE)
    public void getMsg(String message) {
        log.info("当前时间：{}，收到一条消息：{}", new Date().toString(), message);
    }
}
