package com.goosuu.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    // 声明普通交换机
    public static final String NORMAL_EXCHANGE = "normal_exchange";

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

        // 创建普通交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, false, null);

        //设置过期时间10s
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().expiration("10000").build();


        for (int i = 1; i < 11; i++) {
            String msg = "Hello,你好：" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "normal", basicProperties, msg.getBytes());
        }

        connection.close();
        channel.close();


    }

}
