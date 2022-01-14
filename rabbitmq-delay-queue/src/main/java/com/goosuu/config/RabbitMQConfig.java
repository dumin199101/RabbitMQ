package com.goosuu.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitMQConfig {

    // 普通队列
    public static final String QUEUE_10 = "QA";

    public static final String QUEUE_40 = "QB";

    // 死信队列
    public static final String DEAD_QUEUE = "QD";

    // 普通交换机
    public static final String DIRECT_EXCHANGE = "X";

    // 死信交换机
    public static final String DEAD_DIRECT_EXCHANGE = "Y";

    // 普通交换机routingkey
    public static final String A_ROUTING_KEY = "XA";
    public static final String B_ROUTING_KEY = "XB";

    // 死信交换机routingkey
    public static final String Y_ROUTING_KEY = "YD";


    @Bean("xExchange")
    public Exchange xExchange(){
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE).durable(true).build();
    }

    @Bean("yExchange")
    public Exchange yExchange(){
        return ExchangeBuilder.directExchange(DEAD_DIRECT_EXCHANGE).durable(true).build();
    }

    @Bean("queue_10")
    public Queue queue_10(){
        HashMap<String, Object> args = new HashMap<>();
        // 绑定死信交换机
        args.put("x-dead-letter-exchange",DEAD_DIRECT_EXCHANGE);
        args.put("x-dead-letter-routing-key",Y_ROUTING_KEY);
        // 设置队列有效期10s
        args.put("x-message-ttl",10000);
        return QueueBuilder.durable(QUEUE_10).withArguments(args).build();
    }

    @Bean("queue_40")
    public Queue queue_40(){
        HashMap<String, Object> args = new HashMap<>();
        // 绑定死信交换机
        args.put("x-dead-letter-exchange",DEAD_DIRECT_EXCHANGE);
        args.put("x-dead-letter-routing-key",Y_ROUTING_KEY);
        // 设置队列有效期10s
        args.put("x-message-ttl",40000);
        return QueueBuilder.durable(QUEUE_40).withArguments(args).build();
    }

    @Bean("queue_dead")
    public Queue queue_dead(){
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }

    @Bean
    public Binding queueABind(@Qualifier("queue_10") Queue queue,@Qualifier("xExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(A_ROUTING_KEY).noargs();
    }

    @Bean
    public Binding queueBBind(@Qualifier("queue_40") Queue queue,@Qualifier("xExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(B_ROUTING_KEY).noargs();
    }

    @Bean
    public Binding queueDBind(@Qualifier("queue_dead") Queue queue,@Qualifier("yExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(Y_ROUTING_KEY).noargs();
    }

}
