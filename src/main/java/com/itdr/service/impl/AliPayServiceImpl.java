package com.itdr.service.impl;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itdr.common.ServerResponse;
import com.itdr.config.pay.Configs;
import com.itdr.mapper.OrderItemMapper;
import com.itdr.mapper.OrderMapper;
import com.itdr.mapper.PayInfoMapper;
import com.itdr.pojo.Order;
import com.itdr.pojo.OrderItem;
import com.itdr.pojo.PayInfo;
import com.itdr.pojo.User;
import com.itdr.service.AliPayService;
import com.itdr.utils.DateUtil;
import com.itdr.utils.JsonUtil;
import com.itdr.utils.ObjectToVOUtil;
import com.itdr.utils.ZxingUtil;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

    private AlipayTradePrecreateResponse test_trade_precreate(Order order, List<OrderItem> orderItems) throws AlipayApiException {
        //读取配置文件信息
        Configs.init("zfbinfo.properties");

        //实例化支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(Configs.getOpenApiDomain(),
                Configs.getAppid(), Configs.getPrivateKey(), "json", "utf-8",
                Configs.getAlipayPublicKey(), Configs.getSignType());

        //创建API对应的request类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

        //获取一个BizContent对象,并转换成json格式
        String str = JsonUtil.obj2String(ObjectToVOUtil.getBizContent(order, orderItems));
        request.setBizContent(str);
        //设置支付宝回调路径(在做回调接口的时候要打开这里并设置正确的回调路径)
        request.setNotifyUrl(Configs.getNotifyUrl_test());
        //获取响应,这里要处理一下异常
        AlipayTradePrecreateResponse response = alipayClient.execute(request);

        //返回响应的结果
        return response;
    }

    @Override
    public ServerResponse pay(User user, Long orderNo) {
        //参数判断
        if (orderNo == null || orderNo < 0) {
            return ServerResponse.defeatedRS("非法参数");
        }

        //判断订单是否存在
        Order o = orderMapper.selectByOrderNoAndUserId(orderNo,user.getId());
        if (o == null) {
            return ServerResponse.defeatedRS("订单不存在！");
        }

        //判断订单和用户是否匹配
        if (o.getUserId() != user.getId()) {
            return ServerResponse.defeatedRS("订单不匹配！");
        }

        //根据订单号查询对应商品详情
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo,user.getId());

        //调用支付宝接口获取支付二维码
        AlipayTradePrecreateResponse response = null;
        try {
            response = test_trade_precreate(o, orderItemList);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ServerResponse.defeatedRS("下单失败");
        }
        //成功执行下一步
        if (response != null && response.isSuccess()) {
            // 将二维码信息串生成图片，并保存，（需要修改为运行机器上的路径）
            String filePath = String.format(Configs.getSavecode_test() + "qr-%s.png",
                    response.getOutTradeNo());
            ZxingUtil.getQRCodeImge(response.getQrCode(), 256, filePath);


            //预下单成功返回信息
            Map map = new HashMap();
            map.put("orderNo", o.getOrderNo());

            map.put("qrCode", filePath);
            return ServerResponse.successRS(map);
        }else {
            return ServerResponse.defeatedRS("下单失败");
        }
    }

    @Override
    public ServerResponse alipayCallback(Map<String, String> map) {

        ServerResponse sr = null;

        //step1:获取ordrNo
        Long orderNo = Long.parseLong(map.get("out_trade_no"));
        //step2:获取流水号
        String tarde_no = map.get("trade_no");
        //step3:获取支付状态
        String trade_status = map.get("trade_status");
        //step4:获取支付时间
        String payment_time = map.get("gmt_payment");

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            //不是要付款的订单
            sr = ServerResponse.defeatedRS("SUCCESS");
            return sr;
        }

        if (order.getStatus() != 10) {
            //防止支付宝重复回调
            sr = ServerResponse.defeatedRS("SUCCESS");
            return sr;
        }

        if (trade_status.equals("TRADE_SUCCESS")) {
            //校验状态码，支付成功
            //更改数据库中订单的状态+更改支付时间+更新时间+删除用过的本地二维码
            order.setStatus(20);
            order.setPaymentTime(DateUtil.strToDate(payment_time));
            orderMapper.updateByPrimaryKey(order);

            //支付成功，删除本地存在的二维码图片
            String str = String.format(Configs.getSavecode_realy()+"qr-%s.png",
                    order.getOrderNo());
            File file = new File(str);
            boolean b = file.delete();

        }

        //保存支付宝支付信息
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(1);
        payInfo.setPlatformStatus(trade_status);
        payInfo.setPlatformNumber(tarde_no);
        payInfo.setUserId(order.getUserId());

        int result = payInfoMapper.insert(payInfo);
        if (result > 0) {
            //支付信息保存成功返回结果SUCCESS，让支付宝不再回调
            sr = ServerResponse.successRS("SUCCESS");
            return sr;
        }
        //支付信息保存失败返回结果
        sr = ServerResponse.defeatedRS("SUCCESS");
        return sr;
    }

    @Override
    public ServerResponse queryOrderPaStatus(User user, Long orderNo) {
        //参数判断
        if (orderNo == null || orderNo < 0) {
            return ServerResponse.defeatedRS("非法参数");
        }

        //判断订单是否存在
        Order o = orderMapper.selectByOrderNoAndUserId(orderNo,user.getId());
        if (o == null) {
            return ServerResponse.defeatedRS("订单不存在！");
        }

        //判断订单和用户是否匹配
        if (o.getStatus() != 20) {
            return ServerResponse.successRS(false);
        }

        return ServerResponse.successRS(true);
    }
}
