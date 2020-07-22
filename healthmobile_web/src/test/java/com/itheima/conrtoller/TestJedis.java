package com.itheima.conrtoller;

import org.junit.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @Description:
 * @Date: Create in 17:48 2020/7/20
 */
public class TestJedis {

    @Test
    public void testMethod() {

        JedisPool pool = new JedisPool("204.44.85.50", 6700);
        Jedis resource = pool.getResource();
        String set = resource.set("123", "321");
        System.out.println(set);
    }
}
