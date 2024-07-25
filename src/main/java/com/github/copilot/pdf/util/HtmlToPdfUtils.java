package com.github.copilot.pdf.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class HtmlToPdfUtils {

    /**
     * Converts an HTML input stream to a PDF output stream
     * @param inputStream Input stream of the HTML file
     * @param outputStream Output stream for the PDF file
     */
    public static void convertToPdf(InputStream inputStream, OutputStream outputStream) {
        // Create a new document with A3 page size
        Document document = new Document(PageSize.A3);
        try {
            // Get a PdfWriter instance
            PdfWriter.getInstance(document, outputStream);
            // Open the document
            document.open();
            // Create an HTMLWorker to parse the HTML
            HTMLWorker htmlWorker = new HTMLWorker(document);
            // Parse the HTML input stream
            htmlWorker.parse(new InputStreamReader(inputStream));
        } catch (DocumentException | IOException e) {
            // Log any exceptions
            log.error(e.getMessage(), e);
        } finally {
            // Close the document
            document.close();
        }
    }
}