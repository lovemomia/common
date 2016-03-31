package cn.momia.common.deal.gateway;

import java.math.BigDecimal;

public class PrepayParam extends MapWrapper {
    private int platform;

    private String outTradeNo;
    private long productId;
    private String productTitle;
    private String productUrl;
    private BigDecimal totalFee;

    private String paymentResultUrl;

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getPaymentResultUrl() {
        return paymentResultUrl;
    }

    public void setPaymentResultUrl(String paymentResultUrl) {
        this.paymentResultUrl = paymentResultUrl;
    }
}
