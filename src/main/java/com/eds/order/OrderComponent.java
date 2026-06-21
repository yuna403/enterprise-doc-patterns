package com.eds.order;

import com.eds.common.Printable;
import com.eds.common.Visitable;

/**
 * 订单组件接口：组合模式的抽象组件
 * 定义订单的基本行为
 */
public interface OrderComponent extends Printable, Visitable {
    double getAmount();
    void process();
}
