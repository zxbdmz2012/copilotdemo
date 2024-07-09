package com.github.copilot.excel.util;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for operations related to iText, a library for creating and manipulating PDFs.
 * This class provides methods to convert Apache POI border styles to iText borders,
 * create fonts for PDF documents, and create cells with specific styles and content for PDF tables.
 */
public class ITextUtil {

    /**
     * Maps Apache POI border styles to iText border objects.
     * This allows for consistent border styles between POI-generated spreadsheets and iText-generated PDFs.
     */
    public static final Map<Short, Border> POI_TO_ITEXT_BORDER = new HashMap<>();
    /**
     * Represents a non-breaking space character in iText.
     * This is used to prevent cell content from being trimmed or altered in layout,
     * ensuring that spaces are preserved in the PDF output.
     */
    public static final String BLANK = "\u00a0";

    static {
        POI_TO_ITEXT_BORDER.put(BorderStyle.NONE.getCode(), Border.NO_BORDER);
        POI_TO_ITEXT_BORDER.put(BorderStyle.DASHED.getCode(), new DashedBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.DASH_DOT.getCode(), new DashedBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.DASH_DOT_DOT.getCode(), new DottedBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.DOTTED.getCode(), new DottedBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.DOUBLE.getCode(), new DoubleBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.HAIR.getCode(), new SolidBorder(0.5f));
        POI_TO_ITEXT_BORDER.put(BorderStyle.MEDIUM.getCode(), new SolidBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.MEDIUM_DASH_DOT.getCode(), new DashedBorder(1.5f));
        POI_TO_ITEXT_BORDER.put(BorderStyle.MEDIUM_DASH_DOT_DOT.getCode(), new DottedBorder(1.5f));
        POI_TO_ITEXT_BORDER.put(BorderStyle.MEDIUM_DASHED.getCode(), new DashedBorder(1.5f));
        POI_TO_ITEXT_BORDER.put(BorderStyle.SLANTED_DASH_DOT.getCode(), new DashedBorder(1));
        POI_TO_ITEXT_BORDER.put(BorderStyle.THICK.getCode(), new SolidBorder(2));
        POI_TO_ITEXT_BORDER.put(BorderStyle.THIN.getCode(), new SolidBorder(0.5f));

    }

    /**
     * Creates a PdfFont object from a font file.
     * This method allows for custom fonts to be used in the generated PDF documents.
     *
     * @param path The path to the font file.
     * @return A PdfFont object representing the font.
     * @throws IOException If there is an error reading the font file.
     */
    public static PdfFont createFont(String path) throws IOException {
        return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H);
    }

    /**
     * Creates a cell for a PDF table with specified rowspan, colspan, text content, and font.
     * This method allows for detailed customization of table cells in the PDF.
     *
     * @param rowspan The number of rows the cell should span.
     * @param colspan The number of columns the cell should span.
     * @param text The text content of the cell.
     * @param font The font to be used for the text content.
     * @return A Cell object ready to be added to a PDF table.
     */
    public static Cell cell(int rowspan, int colspan, String text, PdfFont font) {
        if (text == null) {
            text = BLANK;
        }
        final Paragraph paragraph = new Paragraph(text);
        if (font != null) {
            paragraph.setFont(font);
        }
        Cell cell = new Cell(rowspan, colspan).add(paragraph).setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0);
        return cell;
    }

    public static Cell cell(String text, PdfFont font) {
        return cell(1, 1, text, font);
    }

    public static Cell cellNull() throws IOException {
        return cell(null, null).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER);
    }
}
