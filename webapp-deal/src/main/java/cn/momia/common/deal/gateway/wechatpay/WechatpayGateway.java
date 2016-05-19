package cn.momia.common.deal.gateway.wechatpay;

import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.core.platform.Platform;
import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.deal.gateway.PaymentGateway;
import cn.momia.common.deal.gateway.RefundParam;
import cn.momia.common.deal.gateway.RefundQueryParam;
import cn.momia.common.webapp.config.Configuration;
import cn.momia.common.deal.gateway.PrepayParam;
import cn.momia.common.deal.gateway.PrepayResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WechatpayGateway extends PaymentGateway {
    private static class PrepayRequestField {
        public static final String APPID = "appid"; //微信公众号id
        public static final String MCH_ID = "mch_id"; //商户id
        public static final String NONCE_STR = "nonce_str"; //随机字符串
        public static final String SIGN = "sign"; //签名
        public static final String BODY = "body"; //商品描述
        public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
        public static final String TOTAL_FEE = "total_fee"; //总金额
        public static final String SPBILL_CREATE_IP = "spbill_create_ip"; //终端IP
        public static final String NOTIFY_URL = "notify_url"; //通知地址
        public static final String PRODUCT_ID = "product_id"; //通知地址
        public static final String OPENID = "openid"; //通知地址
        public static final String TRADE_TYPE = "trade_type";
        public static final String TIME_EXPIRE = "time_expire";
        public static final String CODE = "code";
    }

    private static class RefundRequestField {
        public static final String APPID = "appid"; //微信公众号id
        public static final String MCH_ID = "mch_id"; //商户id
        public static final String NONCE_STR = "nonce_str"; //随机字符串
        public static final String SIGN = "sign"; //签名
        public static final String TRANSACTION_ID = "transaction_id"; //商品描述
        public static final String OUT_REFUND_NO = "out_refund_no"; //商户订单号
        public static final String TOTAL_FEE = "total_fee"; //总金额
        public static final String REFUND_FEE = "refund_fee"; //终端IP
        public static final String OP_USER_ID = "op_user_id";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatpayGateway.class);

    private static final String DATE_FORMAT_STR = "yyyyMMddHHmmss";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_STR);

    private static final String PREPAY_REQUEST_RETURN_CODE = "return_code";
    private static final String PREPAY_REQUEST_RETURN_MSG = "return_msg";
    private static final String PREPAY_REQUEST_RESULT_CODE = "result_code";
    private static final String PREPAY_REQUEST_PREPAY_ID = "prepay_id";

    private static final String SUCCESS = "SUCCESS";

    @Override
    public PrepayResult prepay(PrepayParam param) {
        PrepayResult result = WechatpayPrepayResult.create(param.getPlatform());

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = createRequest(param);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new MomiaErrorException("fail to execute request: " + request);

            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            processResponseEntity(result, entity, param.getPlatform());
        } catch (Exception e) {
            LOGGER.error("fail to prepay", e);
            result.setSuccessful(false);
        }

        return result;
    }

    private HttpPost createRequest(PrepayParam param) {
        HttpPost httpPost = new HttpPost(Configuration.getString("Payment.Wechat.PrepayService"));
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/xml");
        StringEntity entity = new StringEntity(MomiaUtil.mapToXml(createRequestParams(param)), "UTF-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private Map<String, String> createRequestParams(PrepayParam param) {
        Map<String, String> requestParams = new HashMap<String, String>();

        int platform = param.getPlatform();
        switch (platform) {
            case Platform.APP:
                requestParams.put(PrepayRequestField.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
                requestParams.put(PrepayRequestField.PRODUCT_ID, String.valueOf(param.getProductId()));
                requestParams.put(PrepayRequestField.MCH_ID, Configuration.getString("Payment.Wechat.AppMchId"));
                break;
            case Platform.WAP:
                requestParams.put(PrepayRequestField.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
                requestParams.put(PrepayRequestField.OPENID, getJsApiOpenId(param.get(PrepayRequestField.CODE)));
                requestParams.put(PrepayRequestField.MCH_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
                break;
            default: new MomiaErrorException("not supported platform type: " + platform);
        }

        requestParams.put(PrepayRequestField.NONCE_STR, WechatpayUtil.createNoncestr(32));
        requestParams.put(PrepayRequestField.BODY, param.getProductTitle());
        requestParams.put(PrepayRequestField.OUT_TRADE_NO, param.getOutTradeNo() + DATE_FORMATTER.format(new Date()));
        requestParams.put(PrepayRequestField.TOTAL_FEE, String.valueOf(param.getTotalFee()));
        requestParams.put(PrepayRequestField.SPBILL_CREATE_IP, param.get("userIp"));
        requestParams.put(PrepayRequestField.NOTIFY_URL, Configuration.getString("Payment.Wechat.NotifyUrl"));
        requestParams.put(PrepayRequestField.TRADE_TYPE, param.get("type").toUpperCase());
        requestParams.put(PrepayRequestField.TIME_EXPIRE, DATE_FORMATTER.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)));
        requestParams.put(PrepayRequestField.SIGN, WechatpayUtil.sign(requestParams, platform));

        return requestParams;
    }

    private static String getJsApiOpenId(String code) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(Configuration.getString("Payment.Wechat.AccessTokenService"))
                    .append("?")
                    .append("appid=").append(Configuration.getString("Payment.Wechat.JsApiAppId"))
                    .append("&")
                    .append("secret=").append(Configuration.getString("Payment.Wechat.JsApiSecret"))
                    .append("&")
                    .append("code=").append(code)
                    .append("&")
                    .append("grant_type=authorization_code");
            HttpGet request = new HttpGet(urlBuilder.toString());
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new MomiaErrorException("fail to execute request: " + request);

            String entity = EntityUtils.toString(response.getEntity());
            JSONObject resultJson = JSON.parseObject(entity);

            if (resultJson.containsKey("openid")) return resultJson.getString("openid");

            throw new MomiaErrorException("fail to get openid");
        } catch (Exception e) {
            throw new MomiaErrorException("fail to get openid");
        }
    }

    private void processResponseEntity(PrepayResult result, String entity, int platform) {
        Map<String, String> params = MomiaUtil.xmlToMap(entity);
        String return_code = params.get(PREPAY_REQUEST_RETURN_CODE);
        String result_code = params.get(PREPAY_REQUEST_RESULT_CODE);

        boolean successful = return_code != null && return_code.equalsIgnoreCase(SUCCESS) && result_code != null && result_code.equalsIgnoreCase(SUCCESS);
        result.setSuccessful(successful);

        if (successful) {
            if (!WechatpayUtil.validateSign(params, platform)) throw new MomiaErrorException("fail to prepay, invalid sign");

            if (Platform.isApp(platform)) {
                result.add(WechatpayPrepayResult.App.Field.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
                result.add(WechatpayPrepayResult.App.Field.PARTNERID, Configuration.getString("Payment.Wechat.AppMchId"));
                result.add(WechatpayPrepayResult.App.Field.PREPAYID, params.get(PREPAY_REQUEST_PREPAY_ID));
                result.add(WechatpayPrepayResult.App.Field.PACKAGE, "Sign=WXPay");
                result.add(WechatpayPrepayResult.App.Field.NONCE_STR, WechatpayUtil.createNoncestr(32));
                result.add(WechatpayPrepayResult.App.Field.TIMESTAMP, String.valueOf(new Date().getTime()).substring(0, 10));
                result.add(WechatpayPrepayResult.App.Field.SIGN, WechatpayUtil.sign(result.getAll(), platform));
            } else if (Platform.isWap(platform)) {
                result.add(WechatpayPrepayResult.JsApi.Field.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
                result.add(WechatpayPrepayResult.JsApi.Field.PACKAGE, "prepay_id=" + params.get(PREPAY_REQUEST_PREPAY_ID));
                result.add(WechatpayPrepayResult.JsApi.Field.NONCE_STR, WechatpayUtil.createNoncestr(32));
                result.add(WechatpayPrepayResult.JsApi.Field.TIMESTAMP, String.valueOf(new Date().getTime()).substring(0, 10));
                result.add(WechatpayPrepayResult.JsApi.Field.SIGN_TYPE, "MD5");
                result.add(WechatpayPrepayResult.JsApi.Field.PAY_SIGN, WechatpayUtil.sign(result.getAll(), platform));
            } else {
                throw new MomiaErrorException("unsupported trade source type: " + platform);
            }
        } else {
            LOGGER.error("fail to prepay: {}/{}/{}", params.get(PREPAY_REQUEST_RETURN_CODE), params.get(PREPAY_REQUEST_RESULT_CODE), params.get(PREPAY_REQUEST_RETURN_MSG));
        }
    }

    @Override
    public boolean refund(RefundParam param) {
        try {
            KeyStore keyStore  = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File("/data/appdatas/weixin/" + (param.getPayType() == PayType.WEIXIN_JSAPI ? "js_cert.p12" : "app_cert.p12")));
            try {
                keyStore.load(instream, (param.getPayType() == PayType.WEIXIN_JSAPI ? Configuration.getString("Payment.Wechat.JsApiMchId") : Configuration.getString("Payment.Wechat.AppMchId")).toCharArray());
            } finally {
                instream.close();
            }

            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, (param.getPayType() == PayType.WEIXIN_JSAPI ? Configuration.getString("Payment.Wechat.JsApiMchId") : Configuration.getString("Payment.Wechat.AppMchId")).toCharArray())
                    .build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" },
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            HttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            HttpPost request = createRefundRequest(param);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new MomiaErrorException("fail to execute request: " + request);

            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            Map<String, String> resultMap = MomiaUtil.xmlToMap(entity);

            String return_code = resultMap.get("return_code");
            String result_code = resultMap.get("result_code");

            boolean successful = return_code != null && return_code.equalsIgnoreCase(SUCCESS) && result_code != null && result_code.equalsIgnoreCase(SUCCESS);
            LOGGER.info("weixin refund return_code/result_code/return_msg: {}/{}/{}", new Object[] { return_code, result_code, resultMap.get("return_msg") });

            if (SUCCESS.equalsIgnoreCase(return_code) && !SUCCESS.equalsIgnoreCase(result_code)) {
                LOGGER.error("refund error: {}/{}", resultMap.get("err_code"), resultMap.get("err_code_des"));
            }

            if (successful) {
                if (!WechatpayUtil.validateSign(resultMap, param.getPayType() == PayType.WEIXIN_JSAPI ? Platform.WAP : Platform.APP)) throw new MomiaErrorException("fail to refund, invalid sign");
            }

            return successful;
        } catch (Exception e) {
            LOGGER.error("fail to refund", e);
        }

        return false;
    }

    private HttpPost createRefundRequest(RefundParam param) {
        HttpPost httpPost = new HttpPost(Configuration.getString("Payment.Wechat.RefundService"));
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/xml");
        StringEntity entity = new StringEntity(MomiaUtil.mapToXml(createRefundRequestParams(param)), "UTF-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private Map<String, String> createRefundRequestParams(RefundParam param) {
        Map<String, String> requestParams = new HashMap<String, String>();

        switch (param.getPayType()) {
            case PayType.WEIXIN_APP:
                requestParams.put(RefundRequestField.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
                requestParams.put(RefundRequestField.MCH_ID, Configuration.getString("Payment.Wechat.AppMchId"));
                requestParams.put(RefundRequestField.OP_USER_ID, Configuration.getString("Payment.Wechat.AppMchId"));
                break;
            case PayType.WEIXIN_JSAPI:
                requestParams.put(RefundRequestField.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
                requestParams.put(RefundRequestField.MCH_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
                requestParams.put(RefundRequestField.OP_USER_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
                break;
            default: throw new MomiaErrorException("无效的支付类型: " + param.getPayType());
        }

        requestParams.put(RefundRequestField.NONCE_STR, WechatpayUtil.createNoncestr(32));
        requestParams.put(RefundRequestField.TRANSACTION_ID, param.getTradeNo());
        requestParams.put(RefundRequestField.OUT_REFUND_NO, String.valueOf(param.getRefundId()));
        requestParams.put(RefundRequestField.TOTAL_FEE, String.valueOf(param.getTotalFee()));
        requestParams.put(RefundRequestField.REFUND_FEE, String.valueOf(param.getRefundFee()));
        requestParams.put(RefundRequestField.SIGN, WechatpayUtil.sign(requestParams, param.getPayType() == PayType.WEIXIN_JSAPI ? Platform.WAP : Platform.APP));

        return requestParams;
    }

    @Override
    public boolean refundQuery(RefundQueryParam param) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = createRefundQueryRequest(param);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) throw new MomiaErrorException("fail to execute request: " + request);

            String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
            Map<String, String> resultMap = MomiaUtil.xmlToMap(entity);

            String return_code = resultMap.get("return_code");
            String result_code = resultMap.get("result_code");

            boolean successful = return_code != null && return_code.equalsIgnoreCase(SUCCESS) && result_code != null && result_code.equalsIgnoreCase(SUCCESS);
            if (successful) {
                if (!WechatpayUtil.validateSign(resultMap, param.getPayType() == PayType.WEIXIN_JSAPI ? Platform.WAP : Platform.APP)) throw new MomiaErrorException("fail to refund, invalid sign");

                String refund_status_0 = resultMap.get("refund_status_0");
                return refund_status_0 != null && refund_status_0.equalsIgnoreCase(SUCCESS);
            }
        } catch (Exception e) {
            LOGGER.error("fail to refund", e);
        }

        return false;
    }

    private HttpPost createRefundQueryRequest(RefundQueryParam param) {
        HttpPost httpPost = new HttpPost(Configuration.getString("Payment.Wechat.RefundQueryService"));
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/xml");
        StringEntity entity = new StringEntity(MomiaUtil.mapToXml(createRefundQueryRequestParams(param)), "UTF-8");
        entity.setContentType("application/xml");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        return httpPost;
    }

    private Map<String, String> createRefundQueryRequestParams(RefundQueryParam param) {
        Map<String, String> requestParams = new HashMap<String, String>();

        switch (param.getPayType()) {
            case PayType.WEIXIN_APP:
                requestParams.put(RefundRequestField.APPID, Configuration.getString("Payment.Wechat.AppAppId"));
                requestParams.put(RefundRequestField.MCH_ID, Configuration.getString("Payment.Wechat.AppMchId"));
                break;
            case PayType.WEIXIN_JSAPI:
                requestParams.put(RefundRequestField.APPID, Configuration.getString("Payment.Wechat.JsApiAppId"));
                requestParams.put(RefundRequestField.MCH_ID, Configuration.getString("Payment.Wechat.JsApiMchId"));
                break;
            default: throw new MomiaErrorException("无效的支付类型: " + param.getPayType());
        }

        requestParams.put(RefundRequestField.NONCE_STR, WechatpayUtil.createNoncestr(32));
        requestParams.put(RefundRequestField.TRANSACTION_ID, param.getTradeNo());
        requestParams.put(RefundRequestField.SIGN, WechatpayUtil.sign(requestParams, param.getPayType() == PayType.WEIXIN_JSAPI ? Platform.WAP : Platform.APP));

        return requestParams;
    }
}
