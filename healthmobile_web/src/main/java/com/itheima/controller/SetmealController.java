package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Setmeal;
import com.itheima.service.CheckGroupService;
import com.itheima.service.SetmealService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * @Author: Nice
 * @Description:
 * @Date: Create in 18:24 2020/7/16
 */
@Controller
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    @Reference
    private CheckGroupService checkGroupService;

    @RequestMapping("/getSetmeal")
    @ResponseBody
    public Result getSetmeal() {
        try {
            List<Setmeal> list = this.setmealService.findAll();
            if (list != null && list.size() > 0) {
                return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.QUERY_SETMEAL_SUCCESS);
    }

    @RequestMapping("/findById")
    @ResponseBody
    public Result findById(Integer id) {
        try {
            // 已完成.交给service层去处理
            // Map<String, Object> data = this.setmealService.queryById(id);
            Setmeal setmeal = this.setmealService.findById(id);
            //checkGroupService.findById()
            if (setmeal != null) {
                return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
    }

    @RequestMapping("/findBasetSetmealById")
    @ResponseBody
    public Result findBasetSetmealById(@RequestParam("id") String id) {
        try {
            Setmeal data = this.setmealService.findBaseSemealById(Integer.parseInt(id));
            if (data != null) {
                return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.QUERY_SETMEALLIST_FAIL);
    }


}
