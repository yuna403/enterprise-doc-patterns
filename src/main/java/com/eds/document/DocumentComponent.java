package com.eds.document;

import com.eds.common.Printable;
import com.eds.common.Visitable;

/**
 * 文档组件接口：组合模式的抽象组件
 */
public interface DocumentComponent extends Printable, Visitable {
    String getContent();
}
