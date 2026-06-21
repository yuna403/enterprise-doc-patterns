package com.eds.filesystem;

import com.eds.common.FileSystemNode;
import com.eds.common.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录类：组合模式的容器节点
 */
public class Directory implements FileSystemNode {
    private final String name;
    private final List<FileSystemNode> children = new ArrayList<>();
    private final String basePath;

    public Directory(String name, String basePath) {
        this.name = name;
        this.basePath = basePath + name + "/";
        new java.io.File(this.basePath).mkdirs();
    }

    public void add(FileSystemNode node) {
        children.add(node);
    }

    public void saveAll() {
        children.forEach(node -> {
            if (node instanceof File) {
                ((File) node).saveToDisk(basePath);
            } else if (node instanceof Directory) {
                ((Directory) node).saveAll();
            }
        });
    }

    public String getBasePath() {
        return basePath;
    }

    public List<FileSystemNode> getChildren() {
        return children;
    }

    @Override
    public void print() {
        System.out.printf("目录: %s\n", name);
        children.forEach(FileSystemNode::print);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        children.forEach(c -> c.accept(visitor));
    }

    @Override
    public String getName() {
        return name;
    }
}
