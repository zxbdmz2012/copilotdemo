package com.github.copilot.pdf;

import com.github.copilot.pdf.util.FreeMarkerUtils;
import com.github.copilot.pdf.util.HtmlToPdfUtils;
import com.github.copilot.pdf.util.OpenHtmlToPdfUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

@Slf4j
public class HtmlToPdfConverter {
    // Template path
    public final static String TEMPLATE_PATH = "templates/";

    public static String convertHtmlToPdf(Map<String, Object> content,String templateFileName, String outputPath ,String htmlFile, String pdfFile ) throws IOException {
        long startTime = System.currentTimeMillis();

        // Generate HTML file from template with specified data
        FreeMarkerUtils.genteratorFile(
                content,
                TEMPLATE_PATH,
                templateFileName,
                outputPath,
                htmlFile);
        // Read the HTML file to be converted
        File tempHtmlFile = new File(outputPath + htmlFile);
        InputStream inputStream = new FileInputStream(tempHtmlFile);
        // Output stream for the generated PDF file
        File outputPdfFile = new File(outputPath + pdfFile);
        OutputStream outputStream = new FileOutputStream(outputPdfFile);

        // Choose conversion method
        boolean useOpenHtmlToPdf = true; // Set to false to use openpdf

        if (useOpenHtmlToPdf) {
            OpenHtmlToPdfUtils.convertToPdfWithCss(inputStream, outputStream);
        } else {
            HtmlToPdfUtils.convertToPdf(inputStream, outputStream);
        }

        // Close streams
        inputStream.close();
        outputStream.close();

        // Delete the temporary HTML file
        if (tempHtmlFile.exists()) {
            tempHtmlFile.delete();
        }

        // Log the time taken for conversion
        log.info("Conversion completed, time taken: {}ms", System.currentTimeMillis() - startTime);

        // Return the path of the generated PDF file
        return outputPdfFile.getAbsolutePath();
    }
}