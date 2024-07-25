package com.github.copilot.pdf.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.stream.Collectors;

@Slf4j
public class OpenHtmlToPdfUtils {

    public static void convertToPdf(InputStream inputStream, OutputStream outputStream) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n")), null);
            builder.toStream(outputStream);
            builder.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void convertToPdfWithCss(InputStream inputStream, OutputStream outputStream) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n")), null);
            builder.toStream(outputStream);
            builder.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}