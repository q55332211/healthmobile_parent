package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.entity.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.Order;
import com.itheima.pojo.Setmeal;
import com.itheima.service.OrderService;
import com.itheima.service.SetmealService;
import com.ithiema.utlis.DateFormatUtil;
import com.ithiema.utlis.RedisMessageConstant;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

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

    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;


    @RequestMapping("/sumbit")
    @ResponseBody
    public Result sumbit(@RequestBody Map<String,String> map) {
        Result result = new Result(false, MessageConstant.ORDERSETTING_FAIL);
        Jedis jedis = null;
        try {
            if (map != null) {
                //验证手机验证码
                String telephone = map.get("telephone").toString();
                String validateCode = map.get("validateCode").toString();
                jedis = jedisPool.getResource();
                if (jedis == null) {
                    throw new Exception("jedis 连接错误");
                }
                String code = jedis.get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
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

    /**
     * 使用订单id，导出套餐信息
     */
    @RequestMapping(value = "/exportSetmealInfo")
    public Result exportSetmealInfo(Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            Map<String, Object> order = this.orderService.getOrderById(id);

            Integer setmealId = (Integer) order.get("setmealId");
            response.setContentType("application/pdf");
            String filename = "exportPDF.pdf";
            // 设置以附件的形式导出
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + filename);
            //创建docment
            Document document = new Document();
            //使用docment创建文件
            PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
            //打开文件设置中文
            document.open();
            // 设置表格字体
            BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);

            //使用中文标识 添加个段落
            // 写PDF数据
            // 输出订单和套餐信息
            /************用户信息**************/
            // 体检人
            document.add(new Paragraph("体检人：" + (String) order.get("member"), font));
            // 体检套餐
            document.add(new Paragraph("体检套餐：" + (String) order.get("setmeal"), font));
            // 体检日期
            document.add(new Paragraph("体检日期：" + (String) order.get("orderDate"), font));
            // 预约类型
            document.add(new Paragraph("预约类型：" + (String) order.get("orderType"), font));
            /************套餐信息**************/
            Setmeal setmeal = this.setmealService.findById(setmealId);
            //创建3列数据表格
            Table table = new Table(3);//创建3列的表格
            // 写表头
            table.addCell(buildCell("项目名称", font));
            table.addCell(buildCell("项目内容", font));
            table.addCell(buildCell("项目解读", font));
            // 写数据
            for (CheckGroup checkGroup : setmeal.getCheckGroups()) {
                table.addCell(buildCell(checkGroup.getName(), font));
                // 组织检查项集合
                StringBuffer checkItems = new StringBuffer();
                for (CheckItem checkItem : checkGroup.getCheckItems()) {
                    checkItems.append(checkItem.getName() + "  ");
                }
                table.addCell(buildCell(checkItems.toString(), font));
                table.addCell(buildCell(checkGroup.getRemark(), font));
            }
            // 将表格加入文档
            document.add(table);
            document.close();
            //关闭文件
            // 下载导出
            // 设置头信息

            //设置响应头 响应信息类型
            //设置下载文件名称
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
    }

    // 传递内容和字体样式，生成单元格
    private Cell buildCell(String content, Font font)
            throws BadElementException {
        Phrase phrase = new Phrase(content, font);
        return new Cell(phrase);
    }
}
