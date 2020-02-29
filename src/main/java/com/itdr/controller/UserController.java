package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.UserVO;
import com.itdr.service.UserService;
import com.itdr.utils.ObjectToVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portal/user/")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("login.do")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse sr = userService.login(username, password);
        //当登录成功在session中保存用户数据
        if (sr.isSuccess()) {
            session.setAttribute("user", sr.getData());
        }
        return sr;
    }

    /**
     * 用户注册
     *
     * @param u
     * @return
     */
    @RequestMapping("register.do")
    public ServerResponse<User> register(User u) {
        return userService.register(u);
    }


    /**
     * 检查邮箱或用户名是否重复
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping("check_valid.do")
    public ServerResponse<User> checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }


    /**
     * 获取登录用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping("get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        UserVO userVO = ObjectToVOUtil.userToUserVO(user);
        return ServerResponse.successRS(userVO);
    }

    /**
     * 获取登录用户详细信息
     *
     * @param session
     * @return
     */
    @RequestMapping("get_information.do")
    public ServerResponse<User> getInformation(HttpSession session) {
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
            return ServerResponse.successRS(user);
    }


    /**
     * 登录状态更新个人信息
     * @param email
     * @param phone
     * @param question
     * @param answer
     * @param session
     * @return
     */
        @RequestMapping("update_information.do")
        public ServerResponse<User> updateInformation(String email,
                                                        String phone,
                                                        String question,
                                                        String answer,HttpSession session) {
            //判断用户是否登录
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ServerResponse.defeatedRS(
                        ConstCode.DEFAULT_FAIL,
                        ConstCode.UserEnum.NO_LOGIN.getDesc());
            }

            return userService.updateInformation(email,phone,question,answer,user);
    }

    /**
     * 用户退出
     * @param session
     * @return
     */
    @RequestMapping("logout.do")
    public ServerResponse<User> logout(HttpSession session) {
        session.removeAttribute("user");
        return ServerResponse.successRS(ConstCode.UserEnum.LOGOUT.getDesc());
    }

    /**
     * 忘记密码
     * @param username
     * @return
     */
    @RequestMapping("forget_get_question.do")
    public ServerResponse<User> forgetGetQuestion(String username) {
        return userService.forgetGetQuestion(username);
    }

    /**
     * 提交问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping("forget_check_answer.do")
    public ServerResponse<User> forgetCheckAnswer(String username,String question,String answer) {
        return userService.forgetCheckAnswer(username,question,answer);
    }

    /**
     * 忘记密码的重设密码,设置新密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @param session
     * @return
     */
    @RequestMapping("forget_reset_password.do")
    public ServerResponse<User> forgetResetPassword(String username,String passwordNew,String forgetToken,HttpSession session) {
        ServerResponse<User> userServerResponse = userService.forgetResetPassword(username, passwordNew, forgetToken);
        if(userServerResponse.isSuccess()){
            session.removeAttribute("user");
        }
        return userServerResponse;
    }

    @RequestMapping("reset_password.do")
    public ServerResponse<User> resetPassword(String passwordOld,String passwordNew,HttpSession session) {
        //判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return userService.resetPassword(user,passwordOld,passwordNew);
    }
}