package com.itdr.config.pay;

import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BizContent {
    private String out_trade_no;
    private String seller_id;
    private String total_amount;
    private String discountableAmount;
    private String undiscountable_amount;
    private String subject;
    private String body;
    private List<GoodsDetail> goods_detail;
    private String operator_id;
    private String store_id;
    private String alipayStoreId;
    private String terminalId;
    private ExtendParams extend_params;
    private String timeout_express;
    private String notify_url;


}
