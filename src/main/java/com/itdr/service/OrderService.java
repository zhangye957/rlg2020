package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface OrderService {
    ServerResponse create(User user, Integer shippingId);
}
