package com.eds.document;

import com.eds.filesystem.Directory;
import com.eds.filesystem.File;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTML保存策略：策略模式的具体策略
 */
public class HtmlSaveStrategy implements SaveStrategy {
    private final String savePath;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public HtmlSaveStrategy(String savePath) {
        this.savePath = savePath.endsWith("/") ? savePath : savePath + "/";
    }

    @Override
    public void save(DocumentComponent doc) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String content = doc.getContent();
        String htmlContent = generateHtml(content, timestamp);

        Directory ordersDir = new Directory("orders", savePath);

        File htmlFile = new File("order_summary_" + timestamp + ".html", htmlContent);
        File textFile = new File("order_summary_" + timestamp + ".txt", content);
        ordersDir.add(htmlFile);
        ordersDir.add(textFile);

        try {
            ordersDir.saveAll();
            System.out.printf("文件保存成功：%s\n", ordersDir.getBasePath());
            System.out.printf("生成文件：order_summary_%s.{html,txt}\n", timestamp);
        } catch (Exception e) {
            System.err.println("文件保存失败：" + e.getMessage());
        }
    }

    private String generateHtml(String content, String timestamp) {
        return "<html>\n" +
                "<head>\n" +
                "    <title>订单摘要 - " + timestamp + "</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>订单摘要</h1>\n" +
                "    <p>生成时间：" + timestamp + "</p>\n" +
                "    <pre>" + content + "</pre>\n" +
                "</body>\n" +
                "</html>";
    }
}
