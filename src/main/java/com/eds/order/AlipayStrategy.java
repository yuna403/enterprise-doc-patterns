package com.eds.order;

/**
 * 支付宝支付策略：策略模式的具体策略
 */
public class AlipayStrategy implements PaymentStrategy {
    @Override
    public void pay(OrderComponent order) {
        System.out.printf("Paid ¥%.2f via Alipay\n", order.getAmount());
    }
}
