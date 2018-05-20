package com.crossoverjie.seconds.kill.service.impl;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Connection;
import redis.clients.jedis.JedisCluster;

/**
 * Created by luozhonghua on 2018/5/18.
 */
public class redisClusterTest {

    public static void main(String[] args) {


        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-mvc.xml");
        RedisTemplate jc = context.getBean(RedisTemplate.class);
        jc.opsForValue().set("name", "yucong");
        jc.opsForValue().set("age", "28");
        jc.opsForValue().set("sex", "男");
        System.out.println(jc.opsForValue().get("name"));
        System.out.println(jc.opsForValue().get("age"));
        System.out.println(jc.opsForValue().get("sex"));


        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring-redisCluster.xml");
        /*JedisCluster jc = context.getBean(JedisCluster.class);
        System.out.println(jc.set("name", "yucong"));
        System.out.println(jc.set("age", "28"));
        System.out.println(jc.set("sex", "男"));

        System.out.println(jc.get("name"));
        System.out.println(jc.get("age"));
        System.out.println(jc.get("sex"));*/
    }

}
