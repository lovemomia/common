package cn.momia.common.deal.gateway.alipay;

import cn.momia.common.deal.gateway.RefundNotifyParam;

public class AlipayRefundNotifyParam extends RefundNotifyParam {
    @Override
    public boolean isSuccessful() {
        try {
            String notifyId = get(AlipayRefundField.NOTIFY_ID);
            if (notifyId == null || !AlipayUtil.verifyResponse(notifyId)) return false;

            return AlipayUtil.validateSign(getAll(), get(AlipayRefundField.SIGN));
        } catch (Exception e) {
            return false;
        }
    }
}
