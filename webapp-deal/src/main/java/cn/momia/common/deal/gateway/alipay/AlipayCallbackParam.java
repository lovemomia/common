package cn.momia.common.deal.gateway.alipay;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.deal.gateway.CallbackParam;
import cn.momia.common.deal.gateway.MapWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class AlipayCallbackParam extends MapWrapper implements CallbackParam {
    private static class Field {
        public static final String NOTIFY_ID = "notify_id"; //通知校验ID
        public static final String SIGN = "sign"; //签名
        public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
        public static final String TOTAL_FEE = "total_fee"; //总金额
        public static final String TRADE_NO = "trade_no"; //支付宝交易号
        public static final String GMT_PAYMENT = "gmt_payment"; //交易付款时间
        public static final String BUYER_ID = "buyer_id"; //买家支付宝帐号
        public static final String TRADE_STATUS = "trade_status";
    }

    public AlipayCallbackParam(Map<String, String> params) {
        addAll(params);
    }

    @Override
    public boolean isPayedSuccessfully() {
        try {
            if (!"TRADE_SUCCESS".equalsIgnoreCase(get(Field.TRADE_STATUS))) return false;

            String notifyId = get(Field.NOTIFY_ID);
            if (notifyId == null || !AlipayUtil.verifyResponse(notifyId)) return false;

            return AlipayUtil.validateSign(getAll(), get(Field.SIGN));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getOrderId() {
        return Long.valueOf(get(Field.OUT_TRADE_NO).substring(3));
    }

    @Override
    public int getPayType() {
        return PayType.ALIPAY;
    }

    @Override
    public String getPayer() {
        return get(Field.BUYER_ID);
    }

    @Override
    public Date getFinishTime() {
        Date finishTime = TimeUtil.castToDate(get(Field.GMT_PAYMENT));
        return finishTime == null ? new Date() : finishTime;
    }

    @Override
    public String getTradeNo() {
        return get(Field.TRADE_NO);
    }

    @Override
    public BigDecimal getTotalFee() {
        return new BigDecimal(get(Field.TOTAL_FEE));
    }
}
