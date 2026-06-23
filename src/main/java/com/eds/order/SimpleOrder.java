package com.eds.order;

import com.eds.common.Visitor;

/**
 * 简单订单：组合模式的叶子节点
 */
public class SimpleOrder implements OrderComponent {
    private final double amount;
    private final String productName;

    public SimpleOrder(double amount, String productName) {
        this.amount = amount;
        this.productName = productName;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    public String getProductName() {
        return productName;
    }

    @Override
    public void process() {
        System.out.printf("处理订单: %s - $%.2f\n", productName, amount);
    }

    @Override
    public void print() {
        System.out.printf("订单: %s - $%.2f\n", productName, amount);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
