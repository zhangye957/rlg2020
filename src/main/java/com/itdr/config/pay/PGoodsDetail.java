package com.itdr.config.pay;

import com.alipay.api.domain.GoodsDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGoodsDetail extends GoodsDetail {

    private String alipay_goods_id;

    private String body;

    private String categories_tree;

    private String goods_category;

    private String goods_id;

    private String goods_name;

    private String price;

    private Long quantity;

    private String show_url;
}
