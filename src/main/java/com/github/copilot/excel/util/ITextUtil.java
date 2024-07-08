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


public class ITextUtil {

    public static final Map<Short, Border> POI_TO_ITEXT_BORDER = new HashMap<>();
    // iText7下的空格用此来代替，因为" "会被截掉
    // 不间断空格\u00a0,主要用在office中,让一个单词在结尾处不会换行显示,快捷键ctrl+shift+space
    // 本来\u00a0的正常表现是半角空格的宽度，但在Itext7中表现出来的是全角空格的宽度
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

    public static PdfFont createFont(String path) throws IOException {
        return PdfFontFactory.createFont(path, PdfEncodings.IDENTITY_H);
    }

    public static Cell cell(int rowspan, int colspan, String text, PdfFont font) {
        if (text == null) {
            // 防止单元格高度坍塌
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
