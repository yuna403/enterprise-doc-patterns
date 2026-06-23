package com.eds.document;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PDF保存策略：使用 OpenPDF 生成真正的 PDF 文档
 * 策略模式 (Strategy Pattern) 的具体实现
 */
public class PdfSaveStrategy implements SaveStrategy {
    private final String savePath;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public PdfSaveStrategy(String savePath) {
        this.savePath = savePath.endsWith("/") ? savePath : savePath + "/";
    }

    @Override
    public void save(DocumentComponent doc) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String content = doc.getContent();

        try {
            byte[] pdfBytes = generatePdf(content, timestamp);

            // 直接写入文件系统（PDF是二进制数据，不能用文本方式保存）
            String ordersPath = savePath + "orders/";
            java.io.File dir = new java.io.File(ordersPath);
            if (!dir.exists()) dir.mkdirs();
            String filePath = ordersPath + "order_summary_" + timestamp + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfBytes);
            }

            System.out.printf("PDF文件保存成功：%s\n", filePath);
        } catch (Exception e) {
            System.err.println("PDF文件保存失败：" + e.getMessage());
        }
    }

    /**
     * 生成 PDF 字节数组
     */
    public static byte[] generatePdf(String content, String timestamp) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);
        document.open();

        BaseFont chineseFont = getChineseFont();

        // 标题
        Paragraph title = new Paragraph("订单摘要", new Font(chineseFont, 20, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 分隔线
        Paragraph line = new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                new Font(chineseFont, 8, Font.NORMAL, java.awt.Color.GRAY));
        line.setAlignment(Element.ALIGN_CENTER);
        line.setSpacingAfter(15);
        document.add(line);

        // 文档信息
        Font infoFont = new Font(chineseFont, 10, Font.NORMAL, java.awt.Color.DARK_GRAY);
        Paragraph info1 = new Paragraph("生成时间：" + timestamp, infoFont);
        info1.setSpacingAfter(4);
        document.add(info1);
        Paragraph info2 = new Paragraph("文档类型：企业订单系统", infoFont);
        info2.setSpacingAfter(15);
        document.add(info2);

        // 分隔线
        document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                new Font(chineseFont, 8, Font.NORMAL, java.awt.Color.GRAY)));

        // 文档内容
        Font contentFont = new Font(chineseFont, 12, Font.NORMAL);
        String[] lines = content.split("\n");
        for (String lineText : lines) {
            String trimmed = lineText.trim();
            if (!trimmed.isEmpty()) {
                Paragraph p = new Paragraph(trimmed, contentFont);
                p.setSpacingAfter(6);
                p.setLeading(18);
                document.add(p);
            }
        }

        // 底部信息
        document.add(new Paragraph("\n"));
        Paragraph footer = new Paragraph(
                "本文档由企业文档系统自动生成 | 仅供内部使用",
                new Font(chineseFont, 8, Font.ITALIC, java.awt.Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    /**
     * 获取中文字体 - 使用 Windows 系统字体
     */
    public static BaseFont getChineseFont() throws Exception {
        String[] fontPaths = {
                "c:/windows/fonts/simsun.ttc,0",
                "c:/windows/fonts/msyh.ttc,0",
                "c:/windows/fonts/simhei.ttf,0"
        };
        for (String fontPath : fontPaths) {
            try {
                return BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ignored) {
            }
        }
        // 回退到内置字体（不支持中文，但不会报错）
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }
}
