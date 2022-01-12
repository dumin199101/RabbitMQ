package com.goosuu.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class MessageConfirmProducer {

    static final String QUEUE_NAME = UUID.randomUUID().toString();

    static final int MSG_COUNT = 1000;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
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

        // 创建队列
        /*
        参数一：队列名称
        参数二：是否定义持久化队列
        参数三：是否独占本次连接
        参数四：是否在不使用的时候自动删除队列
        参数五：队列其他参数
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 开启消息确认模式
        channel.confirmSelect();

        // 单个确认
//        singleConfirm(channel);
        // 批量确认
//        batchConfirm(channel);
        // 异步确认
        asyncConfirm(channel);

        //释放资源
        channel.close();
        connection.close();
    }

    private static void asyncConfirm(Channel channel) throws IOException {
        /**
         *  线程安全的哈希表，适合高并发场景
         */
        ConcurrentSkipListMap<Long, String> map = new ConcurrentSkipListMap<>();

        /**
         * @deliveryTag: 消息序列号
         * @multiple:
         *    true：确认小于等于当前序列号的消息
         *    false: 确认当前序列号的消息
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple == true) {
                // 如果是批量确认,删除已经应答的，剩下的就是未应答的
                // 返回小于等于当前序列号的确认消息
                ConcurrentNavigableMap<Long, String> longStringConcurrentNavigableMap = map.headMap(deliveryTag, true);
                longStringConcurrentNavigableMap.clear();
            } else {
                // 如果是单个确认,删除已经应答的，剩下的就是未应答的
                map.remove(deliveryTag);
            }
            System.out.println("消息应答成功,序列号："+deliveryTag);
        };

        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            String s = map.get(deliveryTag);
            System.out.println("消息应答失败：" + s + ",序列号：" + deliveryTag);
        };

        // 添加一个异步监听器
        channel.addConfirmListener(ackCallback, nackCallback);


        // 记录开始发布时间
        long begin = System.currentTimeMillis();


        // 要发送的消息
        for (int i = 1; i <= MSG_COUNT; i++) {
            String message = i + "Hello,我是你的宝贝";
            // 消息持久化
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            // 消息确认
            map.put(channel.getNextPublishSeqNo(),message);
        }


        // 记录结束发布时间
        long end = System.currentTimeMillis();

        System.out.println("异步确认发布：发布消息总数:" + MSG_COUNT + ",耗时为：" + (end - begin) + "ms");
    }

    private static void batchConfirm(Channel channel) throws IOException, InterruptedException {
        // 记录开始发布时间
        long begin = System.currentTimeMillis();

        int outstandingCount = 0;
        int batchCount = 100;

        // 要发送的消息
        for (int i = 1; i <= MSG_COUNT; i++) {
            String message = i + "Hello,我是你的宝贝";
            // 消息持久化
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            // 等待消息确认,批量确认，100次确认一次
            if (outstandingCount % batchCount == 0) {
                channel.waitForConfirms();
                outstandingCount = 0;
            }
            outstandingCount++;
        }


        // 记录结束发布时间
        long end = System.currentTimeMillis();

        System.out.println("批量确认发布：发布消息总数:" + MSG_COUNT + ",耗时为：" + (end - begin) + "ms");
    }

    private static void singleConfirm(Channel channel) throws IOException, InterruptedException {
        // 记录开始发布时间
        long begin = System.currentTimeMillis();

        // 要发送的消息
        for (int i = 1; i <= MSG_COUNT; i++) {
            String message = i + "Hello,我是你的宝贝";
            // 消息持久化
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            // 等待消息确认
            channel.waitForConfirms();
        }

        // 记录结束发布时间
        long end = System.currentTimeMillis();

        System.out.println("单个确认发布：发布消息总数:" + MSG_COUNT + ",耗时为：" + (end - begin) + "ms");
    }
}
