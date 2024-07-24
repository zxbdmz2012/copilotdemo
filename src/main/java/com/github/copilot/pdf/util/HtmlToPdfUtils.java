package com.github.copilot.pdf.util;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.FOUserAgent;
import net.sf.saxon.TransformerFactoryImpl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class HtmlToPdfUtils {

    public static void convertToPdf(InputStream inputStream, OutputStream outputStream) {
        try {
            // Setup FOP factory
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            // Setup SAX transformer
            TransformerFactory transformerFactory = new TransformerFactoryImpl();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File("src/main/resources/xsl-file.xsl")));
            // Setup input and output
            StreamSource source = new StreamSource(inputStream);
            SAXResult result = new SAXResult(fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream).getDefaultHandler());

            // Perform transformation
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}