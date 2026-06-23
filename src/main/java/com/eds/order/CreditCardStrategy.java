package com.eds.order;

/**
 * 信用卡支付策略：策略模式的具体策略
 */
public class CreditCardStrategy implements PaymentStrategy {
    private final String cardNumber;

    public CreditCardStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(OrderComponent order) {
        System.out.printf("Paying $%.2f via Credit Card: ****-****-****-%s\n",
                order.getAmount(), cardNumber.substring(cardNumber.length() - 4));
    }
}
