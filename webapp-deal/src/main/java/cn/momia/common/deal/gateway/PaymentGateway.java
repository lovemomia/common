package cn.momia.common.deal.gateway;

import com.google.common.base.Function;

public abstract class PaymentGateway {
    public abstract PrepayResult prepay(PrepayParam param);

    public CallbackResult callback(CallbackParam param, Function<CallbackParam, Boolean> callback) {
        return callback.apply(param) ? CallbackResult.SUCCESS : CallbackResult.FAILED;
    }

    public abstract boolean refund(RefundParam param);
    public abstract boolean refundNotify(RefundNotifyParam param);
    public abstract boolean refundQuery(RefundQueryParam param);
}