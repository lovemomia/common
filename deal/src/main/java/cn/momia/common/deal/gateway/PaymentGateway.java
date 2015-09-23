package cn.momia.common.deal.gateway;

public interface PaymentGateway {
    PrepayResult prepay(PrepayParam param);
}