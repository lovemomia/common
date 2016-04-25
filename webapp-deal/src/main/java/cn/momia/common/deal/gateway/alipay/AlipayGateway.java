package cn.momia.common.deal.gateway.alipay;

import cn.momia.common.deal.gateway.PaymentGateway;
import cn.momia.common.deal.gateway.RefundNotifyParam;
import cn.momia.common.deal.gateway.RefundParam;
import cn.momia.common.deal.gateway.RefundQueryParam;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.deal.gateway.PrepayResult;
import cn.momia.common.core.platform.Platform;
import cn.momia.common.deal.gateway.PrepayParam;
import org.apache.commons.lang3.StringUtils;

public class AlipayGateway extends PaymentGateway {
    @Override
    public PrepayResult prepay(PrepayParam param) {
        PrepayResult result = new AlipayPrepayResult();

        if (Platform.isApp(param.getPlatform())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.AppService"));
        } else if (Platform.isWap(param.getPlatform())) {
            result.add(AlipayPrepayResult.Field.SERVICE, Configuration.getString("Payment.Ali.WapService"));
            result.add(AlipayPrepayResult.Field.RETURN_URL, param.getPaymentResultUrl());
        }

        result.add(AlipayPrepayResult.Field.PARTNER, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.INPUT_CHARSET, "utf-8");
        result.add(AlipayPrepayResult.Field.SIGN_TYPE, "RSA");
        result.add(AlipayPrepayResult.Field.NOTIFY_URL, Configuration.getString("Payment.Ali.NotifyUrl"));
        result.add(AlipayPrepayResult.Field.OUT_TRADE_NO, param.getOutTradeNo());
        result.add(AlipayPrepayResult.Field.SUBJECT, param.getProductTitle());
        result.add(AlipayPrepayResult.Field.PAYMENT_TYPE, "1");
        result.add(AlipayPrepayResult.Field.SELLER_ID, Configuration.getString("Payment.Ali.Partner"));
        result.add(AlipayPrepayResult.Field.TOTAL_FEE, String.valueOf(param.getTotalFee()));
        result.add(AlipayPrepayResult.Field.BODY, param.getProductTitle());
        result.add(AlipayPrepayResult.Field.IT_B_PAY, "30m");
        result.add(AlipayPrepayResult.Field.SHOW_URL, param.getProductUrl());
        result.add(AlipayPrepayResult.Field.SIGN, AlipayUtil.sign(result.getAll(), param.getPlatform()));

        result.setSuccessful(!StringUtils.isBlank(result.get(AlipayPrepayResult.Field.SIGN)));

        return result;
    }

    @Override
    public boolean refund(RefundParam param) {
        return false;
    }

    @Override
    public boolean refundNotify(RefundNotifyParam param) {
        return false;
    }

    @Override
    public boolean refundQuery(RefundQueryParam param) {
        throw new RuntimeException("暂不支持");
    }
}
