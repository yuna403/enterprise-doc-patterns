package com.eds.order;

/**
 * 经理审批者：责任链模式的具体处理者
 * 处理金额小于1000的订单
 */
public class ManagerApprover extends Approver {
    @Override
    protected boolean canApprove(OrderComponent order) {
        return order.getAmount() < 1000;
    }

    @Override
    protected void processApproval(OrderComponent order) {
        System.out.println("经理审批通过");
    }
}
