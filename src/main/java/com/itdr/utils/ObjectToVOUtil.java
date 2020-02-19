package com.itdr.utils;

import com.itdr.pojo.User;
import com.itdr.pojo.bo.UserVO;

public class ObjectToVOUtil {

    public static UserVO UserToUserVO(User u) {
        UserVO uv = new UserVO();
        uv.setId(u.getId());
        uv.setUsername(u.getUsername());
        uv.setEmail(u.getEmail());
        uv.setPhone(u.getPhone());
        uv.setCreateTime(u.getCreateTime());
        uv.setUpdateTime(u.getUpdateTime());
        return uv;
    }
}
