package com.goosuu.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RouteConsumer2 {


    //交换机名称
    static final String DIRECT_EXCHAGE = "direct_exchange";
    //队列名称
    static final String DIRECT_QUEUE_2 = "direct_queue_2";

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

        // 创建交换机
        /**
         * 声明交换机
         * 参数1：交换机名称
         * 参数2：交换机类型，fanout、topic、direct、headers
         * 参数3： 是否持久化
         * 参数4： 是否自动删除
         * 参数5： 内部使用，一般为false
         * 参数6： 其他参数
         */
        channel.exchangeDeclare(DIRECT_EXCHAGE, BuiltinExchangeType.DIRECT,true,false,false,null);

        // 创建队列
        /**
         * 参数1：队列名称
         * 参数2：是否定义持久化队列
         * 参数3：是否独占本次连接
         * 参数4：是否在不使用的时候自动删除队列
         * 参数5：队列其它参数
         */
        channel.queueDeclare(DIRECT_QUEUE_2,true,false,false,null);


        // 绑定交换机跟队列
        /**
         *  参数1：队列名称
         *  参数2: 交换机名称
         *  参数3： 路由键，routingkey。如果交换机类型为fanout，routingkey设置为空
         */
        channel.queueBind(DIRECT_QUEUE_2,DIRECT_EXCHAGE,"error");
        channel.queueBind(DIRECT_QUEUE_2,DIRECT_EXCHAGE,"info");
        channel.queueBind(DIRECT_QUEUE_2,DIRECT_EXCHAGE,"warning");

        // 设置消息处理
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //收到的消息
                System.out.println("消费者2接收到的消息：" + new String(body, "UTF-8"));
                System.out.println("================================================================");
            }
        };
        /*
        监听消息
        参数一：队列名称
        参数二：是否自动确认，设置为true表示消息接收到自动向mq回复接收到了，mq接收到回复后会删除消息；设置为false则需要手动确认
         */
        channel.basicConsume(DIRECT_QUEUE_2,true,consumer);

    }

}
