package com.eds.order;

/**
 * 审批者抽象类：责任链模式的抽象处理者
 */
public abstract class Approver {
    protected Approver successor;

    public Approver setSuccessor(Approver successor) {
        this.successor = successor;
        return this;
    }

    public void approve(OrderComponent order) {
        if (canApprove(order)) {
            processApproval(order);
        } else if (successor != null) {
            successor.approve(order);
        } else {
            System.out.println("订单需要更高权限审批");
        }
    }

    protected abstract boolean canApprove(OrderComponent order);
    protected abstract void processApproval(OrderComponent order);
}
