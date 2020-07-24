package com.itheima.conrtoller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.controller.SetmealController;
import com.itheima.entity.Result;
import com.ithiema.utlis.SMSUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @Author: Nice
 * @Description:
 * @Date: Create in 9:17 2020/7/18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:springmvc.xml")
public class TestAll {

    @Autowired
    private JedisPool jedisPool;


    @Test
    public void testFunction() {

    }

    @Test
    public void testSMS() {
       /* try {
            SMSUtils.sendShortMessage("SMS_196654009","13680553181","3215");
        } catch (ClientException e) {
            e.printStackTrace();
        }*/
    }


    @Test
    public void testJedis() {
        //  Jedis jedis = jedisPool.getResource();
    /*    String string = jedis.get("123");
        System.out.println(string);*/
   /*     Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String set = jedis.set("13680553181", "5566");
            System.out.println(set);
        } finally {
            jedis.close();
        }*/
    /*    ShardedJedis jedis = shardedJedisPool.getResource();
        System.out.println(jedis.get("123"));*/

    }
}
