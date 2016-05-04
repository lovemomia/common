package cn.momia.common.deal.gateway;

public class PayType {
    public static final int ALIPAY = 1;
    public static final int WEIXIN = 2;
    public static final int FREEPAY = 3;

    public static final int WEIXIN_APP = 21;
    public static final int WEIXIN_JSAPI = 22;

    public static boolean isAlipay(int payType) {
        return payType == ALIPAY;
    }

    public static boolean isWeixinPay(int payType) {
        return payType == WEIXIN ||
                payType == WEIXIN_APP ||
                payType == WEIXIN_JSAPI;
    }
}
