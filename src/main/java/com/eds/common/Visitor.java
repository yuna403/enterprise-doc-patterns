package com.eds.common;

import com.eds.order.OrderComponent;

/**
 * 访问者接口：定义访问操作
 */
public interface Visitor {
    void visit(Printable printable);
    void visit(FileSystemNode node);
    void visit(OrderComponent order);
}
