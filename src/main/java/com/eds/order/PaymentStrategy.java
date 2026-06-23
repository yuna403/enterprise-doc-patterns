package com.eds.order;

/**
 * 支付策略接口：策略模式的抽象策略
 */
public interface PaymentStrategy {
    void pay(OrderComponent order);
}
