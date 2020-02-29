package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface CartService {

    ServerResponse list(User user);

    ServerResponse add(Integer productId, Integer count,Integer type, User user);

    ServerResponse update(Integer productId, Integer count, Integer type,User user);

    ServerResponse delete(Integer productId, User user);

    ServerResponse deleteAll(User user);

    ServerResponse getCartProductCount(User user);

    ServerResponse checked(Integer productId,Integer type, User user);

    ServerResponse over(User user);
}
