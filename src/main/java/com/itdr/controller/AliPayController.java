package com.itdr.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.config.pay.Configs;
import com.itdr.pojo.User;
import com.itdr.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/portal/ali/")
public class AliPayController {

    @Autowired
    AliPayService aliPayService;

    /**
     * 支付宝支付
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("pay.do")
    public ServerResponse pay(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }

        return aliPayService.pay(user,orderNo);
    }

    /**
     * 支付宝回调接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("pay_callback.do")
    public String payCallback(HttpServletRequest request, HttpServletResponse response) {
        ServerResponse sr = null;

        //获取支付宝返回的参数，返回一个map集合
        Map<String, String[]> map = request.getParameterMap();

        //获取该集合的迭代器
        Iterator<String> iterator = map.keySet().iterator();

        //创建一个新的map集合用于存储支付宝的数据，去除不必要的数据
        Map<String, String> params = new HashMap<>();

        //遍历原始集合，键的集合中数据
        while (iterator.hasNext()) {
            String next = iterator.next();
            String[] strings = map.get(next);
            String values = "";
            for (int i = 0; i < strings.length; i++) {
                //如果只有一个元素，就保存一个元素
                //有多个元素时，每个元素之间用逗号隔开
                values = (i == strings.length - 1) ? values + strings[i] : values + strings[i] + ",";
            }
            //放入新的集合
            params.put(next, values);
        }

        //setp1:支付宝验签，是不是支付宝发送的请求，避免重复请求
        try {
            //去除参数中的这个参数（官方提示不需要）
            params.remove("sign_type");
            //调用支付宝封装的方法进行验签操作，需要返回数据+公钥+编码集+类型定义
            boolean result = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!result) {
                //验签失败，返回错误信息
                System.out.println("验签失败");
                //数据转换成json数据格式
                //数据返回给浏览器
                return "FAILED";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            System.out.println("验签失败");
            return "FAILED";
        }
        //官方文档中还有很多需要验证的参数


        sr = aliPayService.alipayCallback(params);

        //防止支付宝成功情况下重复请求
        if (sr.isSuccess()){
            return "SUCCESS";
        }else {
            return "FAILED";
        }
    }

    /**
     * 查询订单状态
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    public ServerResponse queryOrderPaStatus(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }

        return aliPayService.queryOrderPaStatus(user,orderNo);
    }
}
