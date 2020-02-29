package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portal/cart/")
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 查看购物车列表
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    public ServerResponse list(HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }

        return cartService.list(user);
    }

    /**
     * 购物车增加商品
     * @param productId
     * @param count
     * @param session
     * @return
     */
    @RequestMapping("add.do")
    public ServerResponse add(Integer productId,
                                @RequestParam(value = "count",required = false,defaultValue = "1")Integer count,
                                @RequestParam(value = "type",required = false,defaultValue = "0")Integer type,
                                HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.add(productId,count,type,user);
    }

    @RequestMapping("update.do")
    public ServerResponse update(Integer productId,
                              @RequestParam(value = "count",required = false,defaultValue = "1")Integer count,
                              @RequestParam(value = "type",required = false,defaultValue = "0")Integer type,
                              HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.update(productId,count,type,user);
    }

    /**
     * 删除一条选中数据
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping("delete.do")
    public ServerResponse delete(Integer productId,HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.delete(productId,user);
    }

    /**
     * 删除多条选择数据
     * @param session
     * @return
     */
    @RequestMapping("deleteAll.do")
    public ServerResponse deleteAll(HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.deleteAll(user);
    }

    /**
     * 查询购物车中商品件数
     * @param session
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    public ServerResponse getCartProductCount(HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.getCartProductCount(user);
    }

    /**
     * 购物车选中/取消 一个/全部商品
     * @param productId
     * @param type
     * @param session
     * @return
     */
    @RequestMapping("checked.do")
    public ServerResponse checked(Integer productId,
                                  @RequestParam(value = "type",required = false,defaultValue = "0")Integer type,
                                  HttpSession session){
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return cartService.checked(productId,type,user);
    }

    /**
     * 购物车去结算
     * @param session
     * @return
     */
    @RequestMapping("/over.do")
    public ServerResponse over(HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }

        return cartService.over(user);
    }
}
