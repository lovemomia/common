package cn.momia.common.deal.gateway;

public abstract class RefundNotifyParam extends MapWrapper {
    private long refundId;
    private int successNum;

    public long getRefundId() {
        return refundId;
    }

    public void setRefundId(long refundId) {
        this.refundId = refundId;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public abstract boolean isSuccessful();
}
