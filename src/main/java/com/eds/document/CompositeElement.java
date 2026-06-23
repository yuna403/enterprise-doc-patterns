package com.eds.document;

import com.eds.common.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 复合元素：组合模式的容器节点
 */
public class CompositeElement implements DocumentComponent {
    private final List<DocumentComponent> children = new ArrayList<>();

    public void add(DocumentComponent component) {
        children.add(component);
    }

    public List<DocumentComponent> getChildren() {
        return children;
    }

    @Override
    public void print() {
        children.forEach(DocumentComponent::print);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        children.forEach(c -> c.accept(visitor));
    }

    @Override
    public String getContent() {
        StringBuilder content = new StringBuilder();
        children.forEach(c -> content.append(c.getContent()).append("\n"));
        return content.toString();
    }
}
