package com.itdr.mapper;

import com.itdr.pojo.Product;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKey(Product record);
}