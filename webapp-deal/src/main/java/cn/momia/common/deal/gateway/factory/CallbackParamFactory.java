package cn.momia.common.deal.gateway.factory;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.deal.gateway.CallbackParam;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.deal.gateway.alipay.AlipayCallbackParam;
import cn.momia.common.deal.gateway.wechatpay.WechatpayCallbackParam;

import java.util.Map;

public class CallbackParamFactory {
    public static CallbackParam create(Map<String, String> params, int payType) {
        switch (payType) {
            case PayType.ALIPAY: return new AlipayCallbackParam(params);
            case PayType.WEIXIN: return new WechatpayCallbackParam(params);
            default: throw new MomiaErrorException("无效的支付类型: " + payType);
        }
    }
}
