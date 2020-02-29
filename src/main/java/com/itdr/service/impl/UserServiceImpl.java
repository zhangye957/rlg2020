package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.config.TokenCache;
import com.itdr.mapper.UserMapper;
import com.itdr.pojo.User;
import com.itdr.service.UserService;
import com.itdr.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;


    @Override
    public ServerResponse login(String username, String password) {
        //参数非空判断
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if(StringUtils.isEmpty(password)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        //MD5加密
        String MD5Password = MD5Util.getMD5Code(password);

        //查询用户
        User u = userMapper.selectByUserNameAndPassword(username,MD5Password);
        if(u == null){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.FAIL_LOGIN.getCode(),
                    ConstCode.UserEnum.FAIL_LOGIN.getDesc());
        }

        //返回成功数据
        return ServerResponse.successRS(u);
    }

    @Override
    public ServerResponse<User> register(User u) {
        //参数非空判断
        if(StringUtils.isEmpty(u.getUsername())){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        if(StringUtils.isEmpty(u.getPassword())){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        if(StringUtils.isEmpty(u.getAnswer())){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_QUESTION.getCode(),
                    ConstCode.UserEnum.EMPTY_QUESTION.getDesc());
        }

        if(StringUtils.isEmpty(u.getPassword())){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_ANSWER.getCode(),
                    ConstCode.UserEnum.EMPTY_ANSWER.getDesc());
        }

        //查找用户是否存在
//        User user = userMapper.selectByUserName(u.getUsername());
//        if(user != null){
//            return ServerResponse.defeatedRS(
//                    ConstCode.UserEnum.EXIST_USER.getCode(),
//                    ConstCode.UserEnum.EXIST_USER.getDesc());
//        }
        ServerResponse<User> username = checkValid(u.getUsername(), "username");
        //查找邮箱是否存在
        ServerResponse<User> email = checkValid(u.getEmail(), "email");
        if(! username.isSuccess() || !email.isSuccess()){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getCode(),
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc());
        }

        //MD5加密
        u.setPassword(MD5Util.getMD5Code(u.getPassword()));

        //注册用户信息
        int insert = userMapper.insert(u);
        if(insert<=0) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.FAIL_USER.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_USER.getDesc());
    }

    @Override
    public ServerResponse<User> userlogininformation(User u) {
        return null;
    }


    @Override
    public ServerResponse<User> checkValid(String str, String type) {
        //参数非空判断
        if(StringUtils.isEmpty(str)){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    "账户或邮箱不能为空");
        }

        if(StringUtils.isEmpty(type)){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    "类型不能为空");
        }

        //查找用户名或邮箱是否存在
        int i = userMapper.selectByUserNameOrEmail(str,type);
        if(i>0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getCode(),
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_MSG.getDesc());
    }

    @Override
    public ServerResponse<User> updateInformation(String email, String phone, String question, String answer,User user) {
        //参数非空判断
//        if(StringUtils.isEmpty(email)){
//            return ServerResponse.defeatedRS(
//                    ConstCode.DEFAULT_FAIL,
//                    "邮箱不能为空");
//        }
//
//        if(StringUtils.isEmpty(phone)){
//            return ServerResponse.defeatedRS(
//                    ConstCode.DEFAULT_FAIL,
//                    "手机号不能为空");
//        }
//
//        if(StringUtils.isEmpty(question)){
//            return ServerResponse.defeatedRS(
//                    ConstCode.DEFAULT_FAIL,
//                    "密码问题不能为空");
//        }
//
//        if(StringUtils.isEmpty(answer)){
//            return ServerResponse.defeatedRS(
//                    ConstCode.DEFAULT_FAIL,
//                    "密码问题答案不能为空");
//        }
        User u = new User();
        u.setId(user.getId());
        u.setEmail(email);
        u.setPhone(phone);
        u.setQuestion(question);
        u.setAnswer(answer);
        int i = userMapper.updateByPrimaryKeySelective(u);
        if(i<=0) {
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    "信息更新失败");
        }
            return ServerResponse.successRS(
                    ConstCode.UserEnum.SUCCESS_USERMSG.getDesc());
    }

    @Override
    public ServerResponse<User> forgetGetQuestion(String username) {
        //参数非空判断
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }

        //该用户是否存在(在前端可以通过ajax方式判断)
        User user = userMapper.selectByUserName(username);
        if(user == null){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.INEXISTENCE_USER.getCode(),
                    ConstCode.UserEnum.INEXISTENCE_USER.getDesc());
        }

        //获取用户密保问题
        String question = user.getQuestion();
        if(StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.NO_QUESTION.getCode(),
                    ConstCode.UserEnum.NO_QUESTION.getDesc());
        }

        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCRSS,question);
    }

    @Override
    public ServerResponse<User> forgetCheckAnswer(String username, String question, String answer) {
        //参数非空判断
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }

        if(StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_QUESTION.getCode(),
                    ConstCode.UserEnum.EMPTY_QUESTION.getDesc());
        }

        if(StringUtils.isEmpty(answer)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_ANSWER.getCode(),
                    ConstCode.UserEnum.EMPTY_ANSWER.getDesc());
        }

        //判断答案是否正确
        int i = userMapper.selectByUserNameAndQuestionAndAnswer(username,question,answer);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.ERROR_ANSWER.getCode(),
                    ConstCode.UserEnum.ERROR_ANSWER.getDesc());
        }

        //返回随机令牌
        String s = UUID.randomUUID().toString();
        //把令牌放入缓存中，这里使用的是Google的guava缓存，后期会使用redis替代
        TokenCache.set("token_" + username, s);
        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCRSS,s);
    }

    @Override
    public ServerResponse<User> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //参数非空判断
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }

        if(StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        if(StringUtils.isEmpty(forgetToken)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }

        //判断缓存中token
        String token = TokenCache.get("token_" + username);
        if (token==null||token.equals("")){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.LOSE_EFFICACY.getCode(),
                    ConstCode.UserEnum.LOSE_EFFICACY.getDesc());
        }
        if (!token.equals(forgetToken)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }

        //MD5加密
        String MD5Password = MD5Util.getMD5Code(passwordNew);


        //重置密码
        int i = userMapper.updateByUserNameAndPasswordNew(username,MD5Password);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode(),
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode(),
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());
    }

    @Override
    public ServerResponse<User> resetPassword(User user,String passwordOld, String passwordNew) {
        //参数非空判断
        if(StringUtils.isEmpty(passwordOld)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        if(StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        //MD5加密，旧密码和新密码都要加密
        String MD5PasswordOld = MD5Util.getMD5Code(passwordOld);
        String MD5PasswordNew = MD5Util.getMD5Code(passwordNew);


        //更新密码
        int i = userMapper.updateByUserNameAndPasswordOldAndPasswordNew(user.getUsername(),MD5PasswordOld,MD5PasswordNew);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode(),
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode(),
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());
    }
}
