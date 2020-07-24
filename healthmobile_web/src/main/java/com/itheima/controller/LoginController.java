package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.ithiema.utlis.RedisMessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Date: Create in 12:05 2020/7/24
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    /**
     * 用户短信登录
     * @param map
     * @param response
     * @return
     */
    @RequestMapping("/check")
    public Result check(@RequestBody Map map, HttpServletResponse response) {
        try {
            Jedis jedis = jedisPool.getResource();
            String phone = map.get("telephone").toString();
            String key = phone + RedisMessageConstant.SENDTYPE_LOGIN;
            String validateCode = jedis.get(key);
            String code = map.get("validateCode").toString();
            //判断验证码是否正确
            if (!validateCode.equals(code)) {
                //不正确返回错误信息
                return new Result(false, MessageConstant.VALIDATECODE_ERROR);
            }
            //正确删除验证码
            jedis.del(key);
            //根据手机号码判断是否已经注册
            Member member = this.memberService.findByPhone(phone);
            if (member == null) {
                //未注册进行数据插入member 自己获取日期
                member.setRegTime(new Date());
                member.setPhoneNumber(phone);
                this.memberService.add(member);
            }
            //已经注册保存登录信息 使用 redis 还是cookie 设置有效时长7天 ？
            Cookie cookie = new Cookie("member_login", phone);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); //7天有效
            response.addCookie(cookie); //cookie添加到response
            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.VALIDATECODE_ERROR);
    }


}
