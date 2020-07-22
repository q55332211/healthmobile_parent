package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.ithiema.utlis.DateFormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Date: Create in 16:30 2020/7/21
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping("/sumbit")
    @ResponseBody
    public Result sumbit(@RequestBody Map map) {
        Jedis jedis = null;
        try {
            if (map != null) {
                //验证手机验证码
                String telephone = map.get("telephone").toString();
                String validateCode = map.get("validateCode").toString();
                jedis = jedisPool.getResource();
                String code = jedis.get(telephone);

                if (validateCode.equals(code)) {
                    ///查询日期是否可以约  查询之前要进行日期转换
                    String orderDate = map.get("orderDate").toString();
                    String date = DateFormatUtil.dateFormat("yyyy-MM-dd", new Date(orderDate));
                    System.out.println(date);
                    // 交给service处理   //todo 未完成 休息下
                    ///插入数据到订单
                    //如果预约成功就发短信
                    Result result = this.orderService.sumbit(map);
                    return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
                }

            }

        } catch (Exception e) {

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return new Result(false, MessageConstant.ORDERSETTING_FAIL);

    }

}
