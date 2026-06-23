package com.eds.filesystem;

import com.eds.common.FileSystemNode;

import java.util.Iterator;
import java.util.Stack;

/**
 * 文件系统迭代器：迭代器模式
 */
public class FileSystemIterator implements Iterator<FileSystemNode> {
    private final Stack<FileSystemNode> stack = new Stack<>();

    public FileSystemIterator(FileSystemNode root) {
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public FileSystemNode next() {
        FileSystemNode node = stack.pop();
        if (node instanceof Directory) {
            Directory dir = (Directory) node;
            for (int i = dir.getChildren().size() - 1; i >= 0; i--) {
                stack.push(dir.getChildren().get(i));
            }
        }
        return node;
    }
}
