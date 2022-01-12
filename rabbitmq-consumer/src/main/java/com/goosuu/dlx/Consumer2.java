package com.goosuu.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer2 {

    public static void main(String[] args) throws IOException, TimeoutException {

        // 1.创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 2.主机地址;默认为 localhost
        factory.setHost("192.168.1.16");
        // 3.连接端口;默认为 5672
        factory.setPort(5672);
        // 4.虚拟主机名称;默认为 /
        factory.setVirtualHost("/itcast");
        // 5.连接用户名；默认为guest
        factory.setUsername("lieyan");
        // 6.连接密码；默认为guest
        factory.setPassword("123456");

        // 创建连接
        Connection connection = factory.newConnection();

        // 创建频道
        Channel channel = connection.createChannel();

        // 创建死信交换机
        channel.exchangeDeclare(Consumer1.DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, false, null);

        // 创建死信队列
        channel.queueDeclare(Consumer1.DEAD_QUEUE, true, false, false, null);

        // 绑定交换机跟队列
        channel.queueBind(Consumer1.DEAD_QUEUE,Consumer1.DEAD_EXCHANGE,"dead");

        // 监听消息
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "utf-8");
            System.out.println("消费者01消费消息：" + message);
        };


        channel.basicConsume(Consumer1.DEAD_QUEUE, true, deliverCallback, s -> {});



    }

}
