package com.eds.order;

/**
 * 总监审批者：责任链模式的具体处理者
 * 处理金额小于5000的订单
 */
public class DirectorApprover extends Approver {
    @Override
    protected boolean canApprove(OrderComponent order) {
        return order.getAmount() < 5000;
    }

    @Override
    protected void processApproval(OrderComponent order) {
        System.out.println("总监审批通过");
    }
}
