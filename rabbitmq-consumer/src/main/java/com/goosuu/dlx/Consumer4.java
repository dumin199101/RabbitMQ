package com.goosuu.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Consumer4 {

    // 声明普通交换机
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    // 声明死信交换机
    public static final String DEAD_EXCHANGE = "dead_exchange";

    // 声明普通队列
    public static final String NORMAL_QUEUE = "normal_queue";

    // 声明死信队列
    public static final String DEAD_QUEUE = "dead_queue";

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

        //创建死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, false, null);

        //正常队列绑定死信队列
        HashMap<String,Object> params = new HashMap<>();

        params.put("x-dead-letter-exchange",DEAD_EXCHANGE);

        params.put("x-dead-letter-routing-key","dead");

        // 创建普通队列
        channel.queueDeclare(NORMAL_QUEUE, true, false, false, params);

        // 创建死信队列
        channel.queueDeclare(DEAD_QUEUE, true, false, false, null);


        // 绑定交换机跟队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"normal");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"dead");



        // 监听消息
        DeliverCallback deliverCallback = (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            if(message.equals("Hello,你好：7")){
                // 第二个参数代表是否重复加入队列
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
                System.out.println("消费者01拒绝消息：" + message);
            }else{
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                System.out.println("消费者01消费消息：" + message);
            }
        };


        channel.basicConsume(NORMAL_QUEUE, false, deliverCallback, s -> {});
    }


}
