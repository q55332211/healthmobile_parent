package com.itheima.controller;

/**
 * @Author: Nice
 * @Description:
 * @Date: Create in 12:03 2020/7/20
 */

import com.itheima.entity.MessageConstant;
import com.itheima.entity.Result;
import com.ithiema.utlis.SMSUtils;
import com.ithiema.utlis.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 短信校验
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
/*
    @Autowired
    private Jedis jedis;*/

    @Autowired
    private JedisPool jedisPool;

    private static Integer liveTime = 300;

    /***
     * 发送验证码
     * @param number
     * @return
     */
    @RequestMapping("/send4Order")
    public Result send4Order(String number) {

        Jedis jedis = null;
        try {
            if (number != null) {
                jedis = jedisPool.getResource();
                Integer code = ValidateCodeUtils.generateValidateCode(4);
                //SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, number, code.toString());
                //根据手机号存到redis
                System.out.println("发送成功");
                jedis.set(number, code.toString());
                jedis.expire(number, liveTime);
                return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
    }


}
