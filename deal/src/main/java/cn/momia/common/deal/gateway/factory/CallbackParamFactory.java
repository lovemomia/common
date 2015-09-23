package cn.momia.common.deal.gateway.factory;

import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.deal.gateway.CallbackParam;
import cn.momia.common.deal.gateway.alipay.AlipayCallbackParam;
import cn.momia.common.deal.gateway.wechatpay.WechatpayCallbackParam;
import cn.momia.service.product.order.Payment;

import java.util.Map;

public class CallbackParamFactory {
    public static CallbackParam create(Map<String, String> params, int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY: return new AlipayCallbackParam(params);
            case Payment.Type.WECHATPAY: return new WechatpayCallbackParam(params);
            default: throw new MomiaFailedException("无效的支付类型: " + payType);
        }
    }
}
