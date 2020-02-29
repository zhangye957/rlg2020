package com.itdr.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.CategoryMapper;
import com.itdr.mapper.ProductMapper;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;
import com.itdr.pojo.vo.ProductVO;
import com.itdr.service.ProductService;
import com.itdr.utils.ObjectToVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse<Category> baseCategory(Integer pid) {
        //参数合法判断
        if(pid == null || pid<0){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getCode(),
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getDesc());
        }

        //根据父id查找直接子类
        List<Category> li =categoryMapper.selectByParentID(pid);

        //返回成功数据
        return ServerResponse.successRS(li);
    }

    @Override
    public ServerResponse<ProductVO> detail(Integer productId) {
        //参数合法判断
        if(productId == null || productId<0){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getCode(),
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getDesc());
        }

        //根据商品id查找商品数据
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus() != 1){
            return ServerResponse.defeatedRS(
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getCode(),
                    ConstCode.ProductEnum.UNLAWFULNESS_PARAM.getDesc());
        }
        //封装VO
        ProductVO productVO = ObjectToVOUtil.productToUserVO(product);

        //返回成功数据
        return ServerResponse.successRS(productVO);
    }



    @Override
    public ServerResponse<ProductVO> list(String word,Integer pageNum,Integer pageSize,String orderBy) {
        //参数合法判断
        if(StringUtils.isEmpty(word)){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    "参数不能为空");
        }

        //模糊查询数据
        String keyWord = "%"+word+"%";

        //排序参数处理
        String[] split = new String[2];
        if(!StringUtils.isEmpty(orderBy)){
            split = orderBy.split("_");
            PageHelper.startPage(pageNum,pageSize,split[0]+" "+split[1]);
        }else{
            PageHelper.startPage(pageNum,pageSize);
        }


        //开启分页
        List<Product> li = productMapper.selectByName(keyWord);
        PageInfo pageInfo = new PageInfo(li);

        //封装VO
        List<ProductVO> liNew = new ArrayList<ProductVO>();
        for(Product product : li){
            ProductVO pvo = ObjectToVOUtil.productToUserVO(product);
            liNew.add(pvo);
        }

        pageInfo.setList(liNew);
        //返回成功数据
        return ServerResponse.successRS(pageInfo);
    }
}
