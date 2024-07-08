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


public class ExcelUtil {
    public static Cell getAboveCell(Cell cell) {
        final int rowIndex = cell.getRowIndex();
        final int columnIndex = cell.getColumnIndex();
        final Sheet sheet = cell.getSheet();
        return getAboveCell(rowIndex, columnIndex, sheet);
    }

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

    public static Cell getLeftCell(Cell cell) {
        final int columnIndex = cell.getColumnIndex();
        if (columnIndex <= 0) {
            return null;
        }
        final Cell leftCell = cell.getRow().getCell(columnIndex - 1);
        return leftCell;
    }

    /**
     * 找到最大行，并获取各列宽百分比值
     *
     * @param sheet
     * @return
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
     * ARGB 十六进制形式 转 数值形式
     * "FFD0CECE" -> [255, 208, 206, 206]
     *
     * @param argbHex
     * @return
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
     * 获取合并单元格集合
     *
     * @param sheet
     * @return
     */
    public static List<CellRangeAddress> getCombineCellList(Sheet sheet) {
        List<CellRangeAddress> list = new ArrayList<>();
        //获得一个 sheet 中合并单元格的数量
        int sheetMergerCount = sheet.getNumMergedRegions();
        //遍历所有的合并单元格
        for (int i = 0; i < sheetMergerCount; i++) {
            //获得合并单元格保存进list中
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }

    /**
     * 获取cell的合并单元格信息
     * 若cell不是合并单元格，返回null
     *
     * @param combineCellList
     * @param cell
     * @return
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
     * 是否是合并单元格中的第一个单元格
     *
     * @param cellRangeAddress
     * @param cell
     * @return
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
        if (value == (int) value) {
            // 如果小数部分为零，则不保留小数
            return String.valueOf((int) value);
        }
        // 如果有小数部分，则保留小数
        return String.valueOf(value);
    }

}
