package com.eds.common;

/**
 * 可访问接口：用于访问者模式
 */
public interface Visitable {
    void accept(Visitor visitor);
}
