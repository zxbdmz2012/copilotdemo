package com.github.copilot.pdf.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.util.Map;

public class HtmlToPdfUtils {

    public static void convertToPdf(Map<String, Object> data, OutputStream outputStream) {
        try (PDDocument pdfDocument = new PDDocument()) {
            // Render HTML content using Thymeleaf
            String htmlContent = renderHtmlContent(data);

            // Create PDF page
            PDPage page = new PDPage();
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(25, 700);

                // Write HTML content to PDF
                contentStream.showText(htmlContent);
                contentStream.newLine();

                contentStream.endText();
            }

            // Save PDF document
            pdfDocument.save(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String renderHtmlContent(Map<String, Object> data) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setSuffix(".html");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process("templates/your-template", context);
    }
}