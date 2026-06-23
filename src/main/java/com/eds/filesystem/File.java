package com.eds.filesystem;

import com.eds.common.FileSystemNode;
import com.eds.common.Visitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件类：组合模式的叶子节点
 */
public class File implements FileSystemNode {
    private final String name;
    private final String content;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public void saveToDisk(String basePath) {
        String filePath = basePath + name;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            System.out.println("保存文件: " + filePath);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    @Override
    public void print() {
        System.out.printf("文件: %s (内容: %s)\n", name, content);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() {
        return name;
    }
}
