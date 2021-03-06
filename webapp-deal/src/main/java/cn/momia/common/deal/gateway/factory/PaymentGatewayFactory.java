package cn.momia.common.deal.gateway.factory;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.deal.gateway.PaymentGateway;

import java.util.Map;

public class PaymentGatewayFactory {
    private static Map<Integer, PaymentGateway> prototypes;

    public void setPrototypes(Map<Integer, PaymentGateway> prototypes) {
        PaymentGatewayFactory.prototypes = prototypes;
    }

    public static PaymentGateway create(int payType) {
        PaymentGateway paymentGateway = prototypes.get(payType);
        if (paymentGateway == null) throw new MomiaErrorException("无效的支付类型: " + payType);

        return paymentGateway;
    }
}
