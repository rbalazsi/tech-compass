package com.robertbalazsi.techcompass.testngcsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataMapper {

    public static List<Object[]> readMappedCSVRecords(URL url, boolean hasHeader, String arrayElementsSeparator, Class<?>... fieldTypes) throws IOException, IllegalArgumentException {
        List<Object[]> objectsList = new ArrayList<Object[]>();
        CSVParser csvParser = CSVParser.parse(url, Charset.forName("UTF-8"), CSVFormat.RFC4180);
        Iterator<CSVRecord> csvIterator = csvParser.iterator();

        int recordSize;
        long lineNumber = 1;
        if (csvIterator.hasNext()) {
            // The first line would also give the number of columns, which is expected to be equal to the number of
            // field types given
            CSVRecord record = csvIterator.next();
            recordSize = record.size();

            if (fieldTypes.length != recordSize) {
                throw new IllegalArgumentException(String.format("Number of transmitted field types is not equal to the record size! It is %d and it should be %d", fieldTypes.length, recordSize));
            }

            if (!hasHeader) {
                objectsList.add(processCSVRecord(record, recordSize, arrayElementsSeparator, fieldTypes));
            }

            lineNumber++;

            while (csvIterator.hasNext()) {
                record = csvIterator.next();

                if (record.size() != recordSize) {
                    throw new IOException(String.format("Line #%d is inconsistent because it contains %d entries! Expected record size is %d.", lineNumber, record.size(), recordSize));
                }

                objectsList.add(processCSVRecord(record, recordSize, arrayElementsSeparator, fieldTypes));
                lineNumber++;
            }
        }

        return objectsList;
    }

    public static List<Object[]> readMappedExcelRecords(URL url, ExcelFormat format, String sheet, boolean hasHeader, String arrayElementsSeparator,
                                                      Class<?>... fieldTypes) throws IOException, InvalidFormatException {
        List<Object[]> objectsList = new ArrayList<Object[]>();
        Workbook workbook = null;
        try {
            File excelFile = FileUtils.toFile(url);
            workbook = (format == ExcelFormat.XLSX) ? new XSSFWorkbook(excelFile) : new HSSFWorkbook(new FileInputStream(excelFile));

            // If the sheet is specified, we open that, otherwise we go with the first sheet in the document
            Sheet worksheet = !StringUtils.isEmpty(sheet) ? workbook.getSheet(sheet) : workbook.getSheetAt(0);

            int recordSize;
            long lineNumber = 1;
            Iterator<Row> rowIterator = worksheet.rowIterator();

            if (rowIterator.hasNext()) {
                /* The first line would also give the number of columns, which is expected to be equal to the number of field
                   types given. Also, data doesn't have to begin on the first column, however, it must be aligned, i.e. if
                   the second column is the first data column on the first row, it must be so in all subsequent ones. */
                Row row = rowIterator.next();
                short firstColumnNr = row.getFirstCellNum();
                short lastColumnNr = row.getLastCellNum();
                recordSize = lastColumnNr - firstColumnNr;

                if (firstColumnNr < 0) {
                    throw new IllegalArgumentException(String.format("Line #%d does not contain any cells.", lineNumber));
                }

                if (fieldTypes.length != recordSize) {
                    throw new IllegalArgumentException(String.format("Number of transmitted field types is not equal to the record size! It is %d and it should be %d", fieldTypes.length, recordSize));
                }

                if (!hasHeader) {
                    objectsList.add(processExcelRow(row, firstColumnNr, lastColumnNr, arrayElementsSeparator, fieldTypes));
                }

                lineNumber++;

                short currentFirstCol, currentLastCol;
                while (rowIterator.hasNext()) {
                    row = rowIterator.next();
                    currentFirstCol = row.getFirstCellNum();
                    currentLastCol = row.getLastCellNum();

                    if (currentLastCol - currentFirstCol - 1 < 1) {
                        continue;
                    }

                    if (currentFirstCol != firstColumnNr || currentLastCol < lastColumnNr) {
                        throw new IOException(String.format("Line #%d is inconsistent because its columns are not aligned with the first row. First data column: %s, last data column: %s", lineNumber, currentFirstCol, currentLastCol));
                    }

                    objectsList.add(processExcelRow(row, firstColumnNr, lastColumnNr, arrayElementsSeparator, fieldTypes));
                    lineNumber++;
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return objectsList;
    }

    public static Object mapValue(Class<?> type, String value, String arrayElementsSeparator) {
        // To return null
        if (value == null || "null".equals(value.toLowerCase())) {
            if (type.isPrimitive()) {
                throw new IllegalArgumentException(String.format("Cannot return null because the given field type '%s' is a primitive type!", type.getName()));
            }

            return null;
        } else if ("<NULL>".equals(value)) {
            if (!type.isArray()) {
                return null;
            }

            // To return an array containing one null element, i.e. new String[] { null }
            return Array.newInstance(type.getComponentType(), 1);
        }

        if (!type.isArray()) {
            return convertStringToSimpleType(type, value);
        }

        // To return an empty array, i.e. new String[] {}
        if ("<EMPTY>".equals(value)) {
            return Array.newInstance(type.getComponentType(), 0);
        }

        String[] values = value.split("\\" + arrayElementsSeparator, -1); //to keep empty strings
        Class<?> componentType = type.getComponentType();
        Object arr = Array.newInstance(componentType, values.length);
        for (int i = 0; i < values.length; i++) {
            if ("null".equals(values[i])) {
                if (componentType.isPrimitive()) {
                    throw new IllegalArgumentException(String.format("Cannot set element to null because the array's component type '%s' is a primitive type!", componentType.getName()));
                }

                ((Object[]) arr)[i] = null;
            } else {
                Array.set(arr, i, convertStringToSimpleType(componentType, values[i]));
            }
        }

        return arr;
    }

    public static Object convertStringToSimpleType(Class<?> type, String value) {
        if (type == String.class) {
            return value;
        } else if (type == byte.class) {
            return Byte.parseByte(value);
        } else if (type == Byte.class) {
            return Byte.valueOf(value);
        } else if (type == short.class) {
            return Short.parseShort(value);
        } else if (type == Short.class) {
            return Short.valueOf(value);
        } else if (type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Integer.class) {
            return Integer.valueOf(value);
        } else if (type == long.class) {
            return Long.parseLong(value);
        } else if (type == Long.class) {
            return Long.valueOf(value);
        } else if (type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (type == float.class) {
            return Float.parseFloat(value);
        } else if (type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Double.class) {
            return Double.valueOf(value);
        }

        throw new IllegalArgumentException(String.format("Class type '%s' is not supported for mapping!", type.getName()));
    }

    private static Object[] processCSVRecord(CSVRecord record, int recordSize, String arrayElementSeparator, Class... fieldTypes) {
        Object[] objectsArr = new Object[recordSize];
        for (int i = 0; i < recordSize; i++) {
            objectsArr[i] = mapValue(fieldTypes[i], record.get(i), arrayElementSeparator);
        }

        return objectsArr;
    }

    private static Object[] processExcelRow(Row row, short firstCol, short lastCol, String arrayElementSeparator, Class... fieldTypes) {
        Object[] objectsArr = new Object[lastCol - firstCol];
        for (short i=firstCol; i<lastCol; i++) {
            Cell cell = row.getCell(i);
            objectsArr[i] = mapValue(fieldTypes[i], getStringFromCell(cell), arrayElementSeparator);
        }

        return objectsArr;
    }

    private static String getStringFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                throw new IllegalArgumentException("Invalid cell type: " + cell.getCellTypeEnum());
        }
    }
}
