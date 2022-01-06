package com.goosuu.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PubSubProducer {

    //交换机名称
    static final String FANOUT_EXCHAGE = "fanout_exchange";
    //队列名称
    static final String FANOUT_QUEUE_1 = "fanout_queue_1";
    //队列名称
    static final String FANOUT_QUEUE_2 = "fanout_queue_2";

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
        channel.exchangeDeclare(FANOUT_EXCHAGE, BuiltinExchangeType.FANOUT,true,false,false,null);

        // 创建队列
        /**
         * 参数1：队列名称
         * 参数2：是否定义持久化队列
         * 参数3：是否独占本次连接
         * 参数4：是否在不使用的时候自动删除队列
         * 参数5：队列其它参数
         */
        channel.queueDeclare(FANOUT_QUEUE_1,true,false,false,null);
        channel.queueDeclare(FANOUT_QUEUE_2,true,false,false,null);

        // 绑定交换机跟队列
        /**
         *  参数1：队列名称
         *  参数2: 交换机名称
         *  参数3： 路由键，routingkey。如果交换机类型为fanout，routingkey设置为空
         */
        channel.queueBind(FANOUT_QUEUE_1,FANOUT_EXCHAGE,"");
        channel.queueBind(FANOUT_QUEUE_2,FANOUT_EXCHAGE,"");

        // 发送消息
        /**
         * 参数1：交换机名称，如果没有指定则使用默认Default Exchage
         * 参数2：路由key,简单模式可以传递队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */
        for (int i = 1; i <=10 ; i++) {
            String message = i + ",Hello,欢迎收看CCTV1";
            channel.basicPublish(FANOUT_EXCHAGE,"",null,message.getBytes());
        }

        // 释放资源
        channel.close();
        connection.close();










    }

}
