package com.github.copilot.pdf.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelToHtmlUtils {

    public static String convertToHtml(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body><table>");

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                htmlBuilder.append("<tr>");
                for (Cell cell : row) {
                    htmlBuilder.append("<td>").append(getCellValue(cell)).append("</td>");
                }
                htmlBuilder.append("</tr>");
            }
        }

        htmlBuilder.append("</table></body></html>");
        workbook.close();
        return htmlBuilder.toString();
    }

    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}