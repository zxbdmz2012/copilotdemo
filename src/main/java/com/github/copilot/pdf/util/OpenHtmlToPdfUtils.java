package com.github.copilot.pdf.util;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.*;
import java.util.stream.Collectors;

@Slf4j
public class OpenHtmlToPdfUtils {

    /**
     * Converts an HTML input stream to a PDF output stream
     * @param inputStream Input stream of the HTML file
     * @param outputStream Output stream for the PDF file
     */
    public static void convertToPdfWithCss(InputStream inputStream, OutputStream outputStream) {

         float width = 12.5f;
         float height = 11.0f;

        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n")), null);
            builder.toStream(outputStream);
            builder.useDefaultPageSize(width,height, BaseRendererBuilder.PageSizeUnits.INCHES);
            builder.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}