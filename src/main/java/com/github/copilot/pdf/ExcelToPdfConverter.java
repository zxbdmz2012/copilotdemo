package com.github.copilot.pdf;

import com.github.copilot.pdf.util.ExcelToHtmlUtils;
import com.github.copilot.pdf.util.OpenHtmlToPdfUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class ExcelToPdfConverter {

    public static void convertExcelToPdf(String excelFilePath, String pdfFilePath) throws IOException {
        try (InputStream excelInputStream = new FileInputStream(excelFilePath);
             OutputStream pdfOutputStream = new FileOutputStream(pdfFilePath)) {

            // Convert Excel to HTML
            String htmlContent = ExcelToHtmlUtils.convertToHtml(excelInputStream);

            // Convert HTML to PDF
            OpenHtmlToPdfUtils.convertToPdf(new ByteArrayInputStream(htmlContent.getBytes()), pdfOutputStream);

            log.info("Excel file converted to PDF successfully.");
        } catch (Exception e) {
            log.error("Error converting Excel to PDF", e);
        }
    }
}