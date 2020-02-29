package com.itdr.mapper;

import com.itdr.pojo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNoAndUserId(@Param("orderNo")Long orderNo,
                                   @Param("userId") Integer uid);


    Order selectByOrderNo(Long orderNo);
}