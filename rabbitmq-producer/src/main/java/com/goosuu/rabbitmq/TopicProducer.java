package com.goosuu.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.goosuu.rabbitmq.RouteProducer.DIRECT_EXCHAGE;

public class TopicProducer {

    //交换机名称
    static final String TOPIC_EXCHAGE = "topic_exchange";
    //队列名称
    static final String TOPIC_QUEUE_1 = "topic_queue_1";
    //队列名称
    static final String TOPIC_QUEUE_2 = "topic_queue_2";

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
        channel.exchangeDeclare(TOPIC_EXCHAGE, BuiltinExchangeType.TOPIC,true,false,false,null);

        // 创建队列
        /**
         * 参数1：队列名称
         * 参数2：是否定义持久化队列
         * 参数3：是否独占本次连接
         * 参数4：是否在不使用的时候自动删除队列
         * 参数5：队列其它参数
         */
        channel.queueDeclare(TOPIC_QUEUE_1,true,false,false,null);
        channel.queueDeclare(TOPIC_QUEUE_2,true,false,false,null);

        // 绑定交换机跟队列
        /**
         *  参数1：队列名称
         *  参数2: 交换机名称
         *  参数3： 路由键，routingkey。
         */
        channel.queueBind(TOPIC_QUEUE_1,TOPIC_EXCHAGE,"item.*");
        channel.queueBind(TOPIC_QUEUE_2,TOPIC_EXCHAGE,"#.add");

        // 发送消息
        /**
         * 参数1：交换机名称，如果没有指定则使用默认Default Exchage
         * 参数2：路由key,简单模式可以传递队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */

        String message = "Hello,生成订单";
        channel.basicPublish(TOPIC_EXCHAGE,"item.add",null,message.getBytes());

        message = "Hello,生成产品订单";
        channel.basicPublish(TOPIC_EXCHAGE,"item.product.add",null,message.getBytes());

        message = "Hello,删除订单";
        channel.basicPublish(TOPIC_EXCHAGE,"item.delete",null,message.getBytes());

        // 释放资源
        channel.close();
        connection.close();


    }
}
