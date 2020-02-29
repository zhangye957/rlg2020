package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.*;
import com.itdr.pojo.*;
import com.itdr.pojo.vo.CartVO;
import com.itdr.pojo.vo.OrderItemVO;
import com.itdr.pojo.vo.OrderVO;
import com.itdr.pojo.vo.ShippingVO;
import com.itdr.service.CartService;
import com.itdr.service.OrderService;
import com.itdr.utils.BigDecimalUtil;
import com.itdr.utils.ObjectToVOUtil;
import com.itdr.utils.PropertiesUtil;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.dc.pr.PRError;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrederServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    CartService cartService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    ShippingMapper shippingMapper;

    //订单编号生成规则
    private Long getNo(){
        long round = Math.round(Math.random() * 100);
        long l = System.currentTimeMillis()+round;
        return l;
    }

    @Override
    public ServerResponse create(User user, Integer shippingId) {
        //参数判断
        if(shippingId == null || shippingId<0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        //判断当前用户购物车中有没有被选中的数据
        ServerResponse over = cartService.over(user);
        if (!over.isSuccess()){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.NOSELECT_PRODUCT.getCode(),
                    ConstCode.CartEnum.NOSELECT_PRODUCT.getDesc());
        }

        //根据用户名获取购物车信息
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());
        CartVO cartVO = ((CartServiceImpl) cartService).getCartVO(cartList);

        //创建一个订单
        Long no = getNo();
        Order o = new Order();
        o.setUserId(user.getId());
        o.setOrderNo(no);
        o.setShippingId(shippingId);
        o.setPayment(cartVO.getCartTotalPrice());
        o.setPaymentType(1);
        o.setPostage(0);
        o.setStatus(10);
        //把创建的订单对象存储到数据库中
        int i = orderMapper.insert(o);
        if(i<=0){
            return ServerResponse.defeatedRS("订单创建失败");
        }

        //创建订单详情
        List<OrderItemVO> itemVOList = new ArrayList<OrderItemVO>();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(user.getId());
            orderItem.setOrderNo(o.getOrderNo());
            if(cart.getChecked() == 1){
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product.getStatus() == 1 && cart.getQuantity() <= product.getStock()){
                    orderItem.setProductId(cart.getProductId());
                    orderItem.setProductName(product.getName());
                    orderItem.setProductImage(product.getMainImage());
                    orderItem.setCurrentUnitPrice(product.getPrice());
                    orderItem.setQuantity(cart.getQuantity());
                    orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
                }
            }
            //把创建的订单详情对象存储到数据库中
            int i2 = orderItemMapper.insert(orderItem);
            if(i2<=0){
                return ServerResponse.defeatedRS("订单详情创建失败");
            }

            OrderItemVO orderItemVO = ObjectToVOUtil.orderItemToOrderItemVO(orderItem);
            itemVOList.add(orderItemVO);
        }

        //清空购物车数据
        int i3 = cartMapper.deleteByUserId(user.getId());

        //返回成功数据
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping == null){
            return ServerResponse.defeatedRS("地址不存在!");
        }
        ShippingVO shippingVO = ObjectToVOUtil.shippingToShippingVO(shipping);
        OrderVO orderVO = getOrderVO(o, shippingId, itemVOList, shippingVO);

        return ServerResponse.successRS(orderVO);
    }

    //返回成功数据
    private  OrderVO getOrderVO(Order o, Integer shippingId, List<OrderItemVO> list, ShippingVO shippingVO){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(orderVO.getOrderNo());
        orderVO.setShippingId(shippingId);
        orderVO.setPayment(orderVO.getPayment());
        orderVO.setPaymentType(orderVO.getPaymentType());
        orderVO.setPostage(orderVO.getPostage());
        orderVO.setStatus(orderVO.getStatus());
        orderVO.setOrderItemVOList(list);
        orderVO.setShippingVO(shippingVO);
        orderVO.setImageHost(PropertiesUtil.getValue("ImageHost"));

        return orderVO;
    }
}
