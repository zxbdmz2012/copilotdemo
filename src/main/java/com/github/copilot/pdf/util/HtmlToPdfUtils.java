package com.github.copilot.pdf.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class HtmlToPdfUtils {

    public static void convertToPdf(InputStream inputStream, OutputStream outputStream) {
        try (PDDocument pdfDocument = new PDDocument()) {
            // 读取 HTML 内容
            String htmlContent = readHtmlContent(inputStream);

            // 使用 Jsoup 解析 HTML
            Document htmlDoc = Jsoup.parse(htmlContent);
            Elements paragraphs = htmlDoc.select("p");

            // 创建 PDF 页面
            PDPage page = new PDPage();
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(25, 700);

                // 写入 HTML 内容到 PDF
                for (Element paragraph : paragraphs) {
                    contentStream.showText(paragraph.text());
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            // 保存 PDF 文档
            pdfDocument.save(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readHtmlContent(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }
}