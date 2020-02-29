package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.pojo.vo.ProductVO;

public interface ProductService {
    ServerResponse<Category> baseCategory(Integer pid);

    ServerResponse<ProductVO> detail(Integer productId);

    ServerResponse<ProductVO> list(String keyword,Integer pageNum,Integer pageSize,String ord);

}
