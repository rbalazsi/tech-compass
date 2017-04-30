package com.robertbalazsi.techcompass.testngcsv.framework;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CSVDataMapper {

    public static List<Object[]> readMappedCSVRecords(URL url, boolean hasHeader, String arrayElementsSeparator, Class<?>... fieldTypes) throws IOException, IllegalArgumentException {
        List<Object[]> objectsList = new ArrayList<Object[]>();
        CSVParser csvParser = CSVParser.parse(url, Charset.forName("UTF-8"), CSVFormat.RFC4180);
        Iterator<CSVRecord> csvIterator = csvParser.iterator();

        int recordSize;
        long lineNumber = 1;
        if (csvIterator.hasNext()) {
            // If the CSV has a header, we skip the first row
            if (hasHeader) {
                recordSize = csvIterator.next().size();
                if (fieldTypes.length != recordSize) {
                    throw new IllegalArgumentException(String.format("Number of transmitted field types is not equal to the record size! It is %d and it should be %d", fieldTypes.length, recordSize));
                }

                lineNumber++;
            }
            // Otherwise the first line would define the expected record size
            else {
                CSVRecord record = csvIterator.next();
                recordSize = record.size();

                objectsList.add(processRow(record, recordSize, arrayElementsSeparator, fieldTypes));
                lineNumber++;
            }

            while (csvIterator.hasNext()) {
                CSVRecord record = csvIterator.next();

                if (record.size() != recordSize) {
                    throw new IOException(String.format("Line #%d is inconsistent because it contains %d entries! Expected record size is %d.", lineNumber, record.size(), recordSize));
                }

                objectsList.add(processRow(record, recordSize, arrayElementsSeparator, fieldTypes));
                lineNumber++;
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

    private static Object[] processRow(CSVRecord record, int recordSize, String arrayElementsSeparator, Class... fieldTypes) {
        Object[] objectsArr = new Object[recordSize];
        for (int i = 0; i < recordSize; i++) {
            objectsArr[i] = mapValue(fieldTypes[i], record.get(i), arrayElementsSeparator);
        }

        return objectsArr;
    }
}
