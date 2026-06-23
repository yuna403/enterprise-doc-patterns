package com.eds.order;

import com.eds.common.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合订单：组合模式的容器节点
 * 可包含多个子订单，总金额为子订单金额之和
 */
public class CompositeOrder implements OrderComponent {
    private final List<OrderComponent> children = new ArrayList<>();

    public void add(OrderComponent child) {
        children.add(child);
    }

    public List<OrderComponent> getChildren() {
        return children;
    }

    @Override
    public void process() {
        System.out.println("===== 处理组合订单 =====");
        children.forEach(OrderComponent::process);
    }

    @Override
    public double getAmount() {
        return children.stream().mapToDouble(OrderComponent::getAmount).sum();
    }

    @Override
    public void print() {
        System.out.printf("组合订单 (共%d个子订单, 总计: $%.2f)\n", children.size(), getAmount());
        children.forEach(OrderComponent::print);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
