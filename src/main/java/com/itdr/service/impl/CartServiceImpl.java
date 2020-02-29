package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.CartMapper;
import com.itdr.mapper.ProductMapper;
import com.itdr.pojo.Cart;
import com.itdr.pojo.Product;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.CartProductVO;
import com.itdr.pojo.vo.CartVO;
import com.itdr.service.CartService;
import com.itdr.utils.BigDecimalUtil;
import com.itdr.utils.ObjectToVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    //获取CartVO对象
    protected CartVO getCartVO(List<Cart> cartList){
        //获取购物车中对应的商品信息
        List<CartProductVO> cartProductVOList = new ArrayList<CartProductVO>();
        boolean bol = true;
        BigDecimal cartTotalPrice = new BigDecimal("0");
        for(Cart cart : cartList){
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());

            //把商品和购物车信息进行数据封装
            if(product != null){
                CartProductVO cartProductVO = ObjectToVOUtil.cartAndProductToVO(cart, product);
                cartProductVOList.add(cartProductVO);

                //计算购物车总价,只计算选中的商品
                if(cartProductVO.getProductChecked() == 1){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
            }
            //判断购物车是否全选
            if(cart.getChecked() == 0){
                bol = false;
            }
        }


        //返回数据
        CartVO cartVO = ObjectToVOUtil.toCartVO(cartProductVOList,bol,cartTotalPrice);
        return cartVO;
    }

    //获取用户购物车列表
    private ServerResponse<List<Cart>> getCarList(User user){
        //查询登录用户的购物车信息
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());

        //用户购物车中是否有数据
        if(cartList.size() == 0){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.EMPTY_CART.getCode(),
                    ConstCode.CartEnum.EMPTY_CART.getDesc());
        }

        return ServerResponse.successRS(cartList);
    }

    //要添加的商品是否在售
    private ServerResponse<Product> online(Integer productId){
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus() != 1){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.INEXISTENCE_PRODUCT.getCode(),
                    ConstCode.ProductEnum.INEXISTENCE_PRODUCT.getDesc());
        }
        return ServerResponse.successRS(product);
    }

    //返回成功后封装好的数据
    private ServerResponse getSuccess(User user){
        //返回封装好的CartVO数据
        //查询登录用户的购物车信息
        ServerResponse<List<Cart>> carList = getCarList(user);
        if(!carList.isSuccess()){
            return carList;
        }
        CartVO cartVO = getCartVO(carList.getData());

        return ServerResponse.successRS(cartVO);
    }

    @Override
    public ServerResponse list(User user) {
        //查询登录用户的购物车信息
        ServerResponse<List<Cart>> carList = getCarList(user);
        if(!carList.isSuccess()){
            return carList;
        }

        CartVO cartVO = getCartVO(carList.getData());
        return ServerResponse.successRS(cartVO);
    }

    @Override
    public ServerResponse add(Integer productId, Integer count,Integer type, User user) {
        //参数合法判断
        if(productId == null || productId < 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        if(count == null || count <= 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        //要添加的商品是否在售
        ServerResponse<Product> online = online(productId);
        if(!online.isSuccess()){
            return online;
        }
        //添加商品库存有没有超出
        if(count > online.getData().getStock()){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.BEYOND_STOCK.getCode(),
                    ConstCode.ProductEnum.BEYOND_STOCK.getDesc());
        }

        //向购物车中增加或更新一条数据
        Cart c = new Cart();
        c.setUserId(user.getId());
        c.setProductId(productId);
        c.setQuantity(count);

        //查询要增加的数据是否存在
        Cart cart = cartMapper.selectByUserIDAndProductID(user.getId(),productId);
        if(cart == null){
            int insert = cartMapper.insert(c);
            if(insert <= 0){
                return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, "添加商品失败");
            }
        }else{
            //根据type决定要执行的更新方法
            if(type == ConstCode.CartEnum.CART_TYPE.getCode()){
                cart.setQuantity(count+cart.getQuantity());
            }else if(type == 1){
                cart.setQuantity(count);
            }
            //更新数据库中数据
            int i = cartMapper.updateByPrimaryKey(cart);
            if(i <= 0){
                return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, "添加商品失败");
            }

        }


        //返回封装好的CartVO数据
        //查询登录用户的购物车信息
        ServerResponse<List<Cart>> carList = getCarList(user);
        if(!carList.isSuccess()){
            return carList;
        }
        CartVO cartVO = getCartVO(carList.getData());

        return ServerResponse.successRS(cartVO);
    }

    @Override
    public ServerResponse update(Integer productId, Integer count, Integer type,User user) {
        //用户在购物车页面使用数值加减器改变数量

        //参数合法判断
        if(productId == null || productId < 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        if(count == null || count <= 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        //要添加的商品是否在售
        ServerResponse<Product> online = online(productId);
        if(!online.isSuccess()){
            return online;
        }

        //查询要更新的数据
        Cart cart = cartMapper.selectByUserIDAndProductID(user.getId(),productId);
            //根据type决定要执行的更新方法
            if(type == ConstCode.CartEnum.CART_TYPE.getCode()){
                cart.setQuantity(count+cart.getQuantity());
            }else if(type == 1){
                cart.setQuantity(count);
            }
            //更新数据库中数据
            int i = cartMapper.updateByPrimaryKey(cart);
            if(i <= 0){
                return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, "添加商品失败");
            }

        //返回封装好的CartVO数据
        //查询登录用户的购物车信息
        ServerResponse<List<Cart>> carList = getCarList(user);
        if(!carList.isSuccess()){
            return carList;
        }
        CartVO cartVO = getCartVO(carList.getData());

        return ServerResponse.successRS(cartVO);
    }

    @Override
    public ServerResponse delete(Integer productId, User user) {
        //参数合法判断
        if(productId == null || productId < 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        //移除购物车中对应的商品
        int i = cartMapper.deleteByUserIDAndProductId(user.getId(),productId);
        if(i <= 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, "移除商品失败");
        }

        return getSuccess(user);
    }

    @Override
    public ServerResponse deleteAll(User user) {
        //移除购物车中对应的商品
        int i = cartMapper.deleteByUserIDAndChecked(user.getId());
        if(i <= 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, "移除商品失败");
        }

        return getSuccess(user);
    }

    @Override
    public ServerResponse getCartProductCount(User user) {
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());
        Integer num = 0;
        for (Cart cart : cartList) {
            num += cart.getQuantity();
        }
        return ServerResponse.successRS(num);
    }

    @Override
    public ServerResponse checked(Integer productId, Integer type,User user) {
        //参数合法判断
        if(productId != null && productId < 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }
        if(type == null || type < 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL,ConstCode.UNLAWFULNESS_PARAM);
        }

        //选中或者取消选中商品
        int i = cartMapper.updateByUserIdOrProductId(user.getId(),productId,type);
        if(i <= 0){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, "状态更改失败");
        }
        return list(user);
    }

    @Override
    public ServerResponse over(User user) {
        //判断当前用户购物车中也没有数据
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());
        if (cartList.size() == 0){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.EMPTY_CART.getCode(),
                    ConstCode.CartEnum.EMPTY_CART.getDesc());
        }

        //购物车中商品也没有被选中
        boolean bol = false;
        for (Cart cart : cartList) {
            if(cart.getChecked() == 1){
                bol = true;
            }
        }
        if(!bol){
            return ServerResponse.defeatedRS(
                    ConstCode.CartEnum.NOSELECT_PRODUCT.getCode(),
                    ConstCode.CartEnum.NOSELECT_PRODUCT.getDesc());
        }

        return ServerResponse.successRS(true);
    }
}
