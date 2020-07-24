package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.ithiema.utlis.DateFormatUtil;
import com.ithiema.utlis.RedisMessageConstant;
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
        Result result = new Result(false, MessageConstant.ORDERSETTING_FAIL);
        Jedis jedis = null;
        try {
            if (map != null) {
                //验证手机验证码
                String telephone = map.get("telephone").toString();
                String validateCode = map.get("validateCode").toString();
                jedis = jedisPool.getResource();
                String code = jedis.get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
                if (validateCode.equals(code)) {
                    //删除redis缓存验证码 设置生存时间==1？ //todo
                    //  jedis.del(telephone+ RedisMessageConstant.SENDTYPE_ORDER);
                    // 交给service处理
                    map.put("orderTpye", Order.ORDERTYPE_WEIXIN);
                    result = this.orderService.sumbit(map);
                    ///插入数据到订单
                    if (result.isFlag()) {
                        //todo 如果预约成功就发短信
                        return result;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;

    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */

    @ResponseBody
    @RequestMapping("/getOrderById")
    public Result getOrderById(Integer id) {
        try {
            Map map = this.orderService.getOrderById(id);
            if (map != null) {
                return new Result(false, MessageConstant.QUERY_ORDER_SUCCESS, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
    }


}
