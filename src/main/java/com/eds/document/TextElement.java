package com.eds.document;

import com.eds.common.Visitor;

/**
 * 文本元素：组合模式的叶子节点
 */
public class TextElement implements DocumentComponent {
    private final String content;

    public TextElement(String content) {
        this.content = content;
    }

    @Override
    public void print() {
        System.out.println(content);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getContent() {
        return content;
    }
}
