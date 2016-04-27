package cn.momia.common.deal.gateway.alipay;

import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.core.util.TimeUtil;
import cn.momia.common.deal.gateway.PaymentGateway;
import cn.momia.common.deal.gateway.RefundParam;
import cn.momia.common.deal.gateway.RefundQueryParam;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.deal.gateway.PrepayResult;
import cn.momia.common.core.platform.Platform;
import cn.momia.common.deal.gateway.PrepayParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlipayGateway extends PaymentGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayGateway.class);

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
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(AlipayRefundField.SERVICE, Configuration.getString("Payment.Ali.RefundService"));
        requestParams.put(AlipayRefundField.PARTNER, Configuration.getString("Payment.Ali.Partner"));
        requestParams.put(AlipayRefundField.INPUT_CHARSET, "utf-8");
        requestParams.put(AlipayRefundField.SIGN_TYPE, "RSA");
        requestParams.put(AlipayRefundField.NOTIFY_URL, Configuration.getString("Payment.Ali.RefundNotifyUrl"));
        requestParams.put(AlipayRefundField.SELLER_USER_ID, Configuration.getString("Payment.Ali.Partner"));
        requestParams.put(AlipayRefundField.REFUND_DATE, TimeUtil.STANDARD_DATE_FORMAT.format(param.getRefundTime()));
        long refundId = param.getRefundId();
        requestParams.put(AlipayRefundField.BATCH_NO, TimeUtil.TIGHT_SHORT_DATE_FORMAT.format(param.getRefundTime()) + (refundId < 100 ? String.format("%03d", refundId) : String.valueOf(refundId)));
        requestParams.put(AlipayRefundField.BATCH_NUM, "1");
        requestParams.put(AlipayRefundField.DETAIL_DATA, StringUtils.join(new String[] { param.getTradeNo(), String.valueOf(param.getRefundFee()), param.getRefundMessage() }, "^"));
        requestParams.put(AlipayRefundField.SIGN, AlipayUtil.refundSign(requestParams));

        try {
            List<String> kvs = new ArrayList<String>();
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                kvs.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8"));
            }

            String refundUrl = Configuration.getString("Payment.Ali.RefundUrl") + "?" + StringUtils.join(kvs, "&");

            LOGGER.debug("{}", refundUrl);

            HttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(refundUrl);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String entity = EntityUtils.toString(response.getEntity());
                Map<String, String> map = MomiaUtil.xmlToMap(entity);
                return "T".equals(map.get("is_success"));
            }
        } catch (Exception e) {
            LOGGER.error("refund error", e);
        }

        return false;
    }

    @Override
    public boolean refundQuery(RefundQueryParam param) {
        throw new RuntimeException("暂不支持");
    }
}
