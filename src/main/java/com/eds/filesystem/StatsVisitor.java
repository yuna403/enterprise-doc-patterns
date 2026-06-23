package com.eds.filesystem;

import com.eds.common.FileSystemNode;
import com.eds.common.Printable;
import com.eds.common.Visitor;
import com.eds.order.OrderComponent;

/**
 * 统计访问者：访问者模式
 */
public class StatsVisitor implements Visitor {
    @Override
    public void visit(Printable printable) {
        if (printable instanceof File) {
            System.out.printf("文件: %s\n", ((File) printable).getName());
        } else if (printable instanceof Directory) {
            Directory dir = (Directory) printable;
            System.out.printf("目录: %s (子节点数: %d)\n", dir.getName(), dir.getChildren().size());
        }
    }

    @Override
    public void visit(FileSystemNode node) {
        node.print();
    }

    @Override
    public void visit(OrderComponent order) {
        order.print();
    }
}
