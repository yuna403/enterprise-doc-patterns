package com.eds.document;

import com.eds.common.Visitor;

/**
 * 格式装饰器抽象类：装饰器模式的抽象装饰器
 */
public abstract class FormatDecorator implements DocumentComponent {
    protected final DocumentComponent component;

    public FormatDecorator(DocumentComponent component) {
        this.component = component;
    }

    @Override
    public void print() {
        component.print();
    }

    @Override
    public void accept(Visitor visitor) {
        component.accept(visitor);
    }

    @Override
    public String getContent() {
        return component.getContent();
    }
}
