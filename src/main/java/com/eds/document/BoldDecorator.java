package com.eds.document;

/**
 * 加粗装饰器：装饰器模式的具体装饰器
 */
public class BoldDecorator extends FormatDecorator {
    public BoldDecorator(DocumentComponent component) {
        super(component);
    }

    @Override
    public void print() {
        System.out.printf("<b>%s</b>\n", component.getContent());
    }
}
