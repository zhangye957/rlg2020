package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

import java.util.Map;

public interface AliPayService {
    ServerResponse pay(User user, Long orderNo);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPaStatus(User user, Long orderNo);

}
