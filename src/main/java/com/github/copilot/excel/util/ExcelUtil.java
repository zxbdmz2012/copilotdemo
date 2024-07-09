package com.github.copilot.excel.util;

import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for Excel-related operations.
 * Provides methods for manipulating and extracting information from Excel cells, rows, and sheets.
 */
public class ExcelUtil {
    /**
     * Retrieves the cell directly above the current cell.
     *
     * @param cell The current cell.
     * @return The cell above the current cell, or null if it's the first row.
     */
    public static Cell getAboveCell(Cell cell) {
        final int rowIndex = cell.getRowIndex();
        final int columnIndex = cell.getColumnIndex();
        final Sheet sheet = cell.getSheet();
        return getAboveCell(rowIndex, columnIndex, sheet);
    }

    /**
     * Retrieves the cell above a specified position in a sheet.
     *
     * @param rowIndex    The row index of the current cell.
     * @param columnIndex The column index of the current cell.
     * @param sheet       The sheet containing the cell.
     * @return The cell above the specified position, or null if it's the first row.
     */
    public static Cell getAboveCell(int rowIndex, int columnIndex, Sheet sheet) {
        if (rowIndex <= 0) {
            return null;
        }
        final Row aboveRow = sheet.getRow(rowIndex - 1);
        if (aboveRow == null) {
            return null;
        }
        final Cell aboveRowCell = aboveRow.getCell(columnIndex);
        return aboveRowCell;
    }

    /**
     * Retrieves the cell to the left of the current cell.
     *
     * @param cell The current cell.
     * @return The cell to the left of the current cell, or null if it's the first column.
     */
    public static Cell getLeftCell(Cell cell) {
        final int columnIndex = cell.getColumnIndex();
        if (columnIndex <= 0) {
            return null;
        }
        final Cell leftCell = cell.getRow().getCell(columnIndex - 1);
        return leftCell;
    }

    /**
     * Finds the row with the maximum number of cells and calculates the width percentages of each column.
     *
     * @param sheet The sheet to analyze.
     * @return An array of UnitValue representing the width percentages of each column.
     */
    public static UnitValue[] findMaxRowColWidths(Sheet sheet) {
        Row maxRow = null;
        int max = 0;
        for (Row row : sheet) {
            final int count = row.getPhysicalNumberOfCells();
            if (count > max) {
                maxRow = row;
                max = count;
            }
        }
        int sum = 0;
        int[] colWidthCache = new int[max];
        for (int i = 0; i < max; i++) {
            final int width = sheet.getColumnWidth(i);
            sum += width;
            colWidthCache[i] = width;
        }
        final UnitValue[] unitValues = new UnitValue[max];
        for (int i = 0; i < colWidthCache.length; i++) {
            final float widthPercent = (float) colWidthCache[i] / sum * 100;
            unitValues[i] = new UnitValue(UnitValue.PERCENT, widthPercent);
        }
        return unitValues;
    }

    /**
     * Converts an ARGB hex string to an array of integers representing the alpha, red, green, and blue components.
     *
     * @param argbHex The ARGB hex string.
     * @return An array of integers representing the ARGB components.
     */
    public static int[] argbHex2argb(String argbHex) {
        String aHex = argbHex.substring(0, 2);
        String rHex = argbHex.substring(2, 4);
        String gHex = argbHex.substring(4, 6);
        String bHex = argbHex.substring(6, 8);
        int a2 = Integer.parseInt(aHex, 16);
        int r2 = Integer.parseInt(rHex, 16);
        int g2 = Integer.parseInt(gHex, 16);
        int b2 = Integer.parseInt(bHex, 16);
        return new int[]{a2, r2, g2, b2};
    }



    public static int[] getColorARGB(Color color) {
        if (color == null) {
            return null;
        }
        int[] result = new int[4];
        if (color instanceof XSSFColor) {
            XSSFColor xc = (XSSFColor) color;
            // final byte[] argb = xc.getARGB();
            final String argbHex = xc.getARGBHex();
            if (argbHex != null) {
                final int[] argb = argbHex2argb(argbHex);
                for (int i = 0; i < argb.length; i++) {
                    if (argb[i] == -1) {
                        result[i] = 255;
                    } else {
                        result[i] = argb[i];
                    }
                }
                // result = new int[]{argb[0], argb[1], argb[2], argb[3]};
            }
        } else if (color instanceof HSSFColor) {
            HSSFColor hc = (HSSFColor) color;
            short[] triplet = hc.getTriplet();
            if (triplet != null) {
                if (triplet[0] == 0 && triplet[1] == 0 && triplet[2] == 0) {
                    // 没有颜色会返回 0, 0, 0，这代表黑色，得重置为 255, 255, 255
                    result = new int[]{0xff, 0xff, 0xff, 0xff};
                } else {
                    result = new int[]{0xff, triplet[0], triplet[1], triplet[2]};
                }
            }
        }
        return result;
    }

    public static int[] getFontColorARGB(Font font, Workbook workbook) {
        if (font == null || workbook == null) {
            return null;
        }
        int[] result = new int[4];
        if (font instanceof XSSFFont) {
            final XSSFColor xssfColor = ((XSSFFont) font).getXSSFColor();
            if (xssfColor == null) {
                return null;
            }
            // byte[] fontColorRGB = xssfColor.getRGB();
            final String argbHex = xssfColor.getARGBHex();
            if (argbHex != null) {
                final int[] argb = argbHex2argb(argbHex);
                for (int i = 0; i < argb.length; i++) {
                    if (argb[i] == -1) {
                        result[i] = 255;
                    } else {
                        result[i] = argb[i];
                    }
                }
            }
        } else if (font instanceof HSSFFont) {
            final HSSFColor hssfColor = ((HSSFFont) font).getHSSFColor(((HSSFWorkbook) workbook));
            if (hssfColor == null) {
                return null;
            }
            short[] triplet = hssfColor.getTriplet();
            if (triplet != null) {
                result = new int[]{0xff, triplet[0], triplet[1], triplet[2]};
            }
        }
        return result;
    }



    /**
     * Retrieves a list of all merged cell ranges within a given sheet.
     * This method iterates through all merged regions in the sheet and adds them to a list.
     *
     * @param sheet The sheet from which to retrieve merged cell ranges.
     * @return A list of CellRangeAddress objects representing all merged regions in the sheet.
     */
    public static List<CellRangeAddress> getCombineCellList(Sheet sheet) {
        List<CellRangeAddress> list = new ArrayList<>();
        int sheetMergerCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }

    /**
     * Determines if a given cell is part of any merged cell range in the list and returns that range.
     * This method checks each merged cell range in the provided list to see if the given cell is within that range.
     *
     * @param combineCellList A list of CellRangeAddress objects representing merged cell ranges.
     * @param cell The cell to check against the list of merged cell ranges.
     * @return The CellRangeAddress that includes the cell, or null if the cell is not part of any merged range.
     */
    public static CellRangeAddress getCombineCellRangeAddress(List<CellRangeAddress> combineCellList, Cell cell) {
        for (CellRangeAddress ca : combineCellList) {
            if (ca.isInRange(cell)) {
                return ca;
            }
        }
        return null;
    }

    /**
     * Checks if the specified cell is the first cell in its merged cell range.
     * This method compares the cell's row and column indices to the first row and column indices of the cell range.
     * It returns true if the cell is the top-left cell in the merged range, indicating it is the first cell.
     *
     * @param cellRangeAddress The CellRangeAddress representing the merged cell range to check.
     * @param cell The cell to check if it is the first cell in the merged range.
     * @return true if the cell is the first cell in the merged range, false otherwise.
     */
    public static boolean isFirstInCombineCell(CellRangeAddress cellRangeAddress, Cell cell) {
        if (cellRangeAddress == null) {
            return false;
        }
        return cell.getRowIndex() == cellRangeAddress.getFirstRow() && cell.getColumnIndex() == cellRangeAddress.getFirstColumn();
    }

    public static String getCellValue(Workbook workbook, Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                cellValue = doubleCoverToString(cell.getNumericCellValue());
                break;
            case STRING:
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                /**
                 * 格式化单元格
                 */
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = getCellValue(evaluator.evaluate(cell));
                break;
            case BLANK:
                cellValue = "";
                break;
            case ERROR:
                cellValue = String.valueOf(cell.getErrorCellValue());
                break;
            case _NONE:
                cellValue = "";
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }

    public static String getCellValue(CellValue formulaValue) {
        String cellValue = "";
        if (formulaValue == null) {
            return cellValue;
        }

        switch (formulaValue.getCellType()) {
            case NUMERIC:
                cellValue = String.valueOf(formulaValue.getNumberValue());
                break;
            case STRING:
                cellValue = String.valueOf(formulaValue.getStringValue());
                break;
            case BOOLEAN:
                cellValue = String.valueOf(formulaValue.getBooleanValue());
                break;
            case BLANK:
                cellValue = "";
                break;
            case ERROR:
                cellValue = String.valueOf(formulaValue.getErrorValue());
                break;
            case _NONE:
                cellValue = "";
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;

    }


    public static String doubleCoverToString(double value) {
        return String.valueOf(value);
    }

}
