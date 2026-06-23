package com.eds.document;

/**
 * 保存策略接口：策略模式
 */
public interface SaveStrategy {
    void save(DocumentComponent doc);
}
