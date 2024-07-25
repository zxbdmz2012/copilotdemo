package com.github.copilot.pdf;

import com.github.copilot.pdf.util.FreeMarkerUtils;
import com.github.copilot.pdf.util.HtmlToPdfUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TestHtmlToPdf
{
    // Template path
    public final static String TEMP = "";

    /**
     * Provides the data required for the template
     * @return Data map
     */
    public static Map<String, Object> getContent()
    {
        return new HashMap<>();
    }

    public static void main(String[] args) throws IOException
    {
        long startTime = System.currentTimeMillis();

        // Generate HTML file from template with specified data
        FreeMarkerUtils.genteratorFile(getContent(),
                TEMP,
                "ProcurementContractTemplate",
                TEMP,
                "afterGeneration.html");

        // Name of the HTML file to be converted
        String htmlFile = "afterGeneration.html";
        // Name of the output PDF file
        String pdfFile = "result.pdf";

        // Read the HTML file to be converted
        InputStream inputStream = new FileInputStream(TEMP + htmlFile);
        // Output stream for the generated PDF file
        OutputStream outputStream = new FileOutputStream(TEMP + pdfFile);

        // Convert HTML to PDF
        HtmlToPdfUtils.convertToPdf(inputStream, outputStream);

        // Log the time taken for conversion
        log.info("Conversion completed, time taken: {}ms", System.currentTimeMillis() - startTime);
    }
}