package com.eds.common;

/**
 * 文件系统节点接口：组合模式的抽象组件
 */
public interface FileSystemNode extends Printable, Visitable {
    String getName();
}
