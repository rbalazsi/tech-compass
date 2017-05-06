package com.robertbalazsi.techcompass.testngcsv;

import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Utility class holding specialized converters from different data formats (like CSV or XLS) to data providers. Each converter is
 * specialized on a given format and provides a reusable {@link DataProvider} for test methods to use.
 */
public class DataSourceConverters {

    private static final String ERR_MISSING_ANNOT = "%s annotation is missing from test method: %s. It is needed when used together with the '%s' converter.";

    /**
     * Converts data from a CSV to a TestNG {@link DataProvider}. It has a {@link Method} parameter that is automatically
     * injected by TestNG as the current test method.<br/>
     * The test method needs to be annotated with {@link CSVDataSource} having as attribute, among others, the URL of the CSV classpath resource.
     * @param testMethod information about the test method
     * @return a collection of Object arrays defining one or more test scenarios
     */
    @DataProvider(name = "csvDataProvider")
    public static Iterator<Object[]> csvDataProvider(Method testMethod) {

        if (!testMethod.isAnnotationPresent(CSVDataSource.class)) {
            throw new IllegalStateException(String.format(ERR_MISSING_ANNOT, "@CSVDataSource", testMethod.getName(), "csvDataProvider"));
        }

        CSVDataSource dsAnnotation = testMethod.getAnnotation(CSVDataSource.class);

        try {
            return DataMapper.readMappedCSVRecords(
                    testMethod.getClass().getResource(dsAnnotation.url()),
                    dsAnnotation.hasHeader(),
                    dsAnnotation.arrayElementSeparator(),
                    testMethod.getParameterTypes()).iterator();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Couldn't load data provider from classpath resource: %s.", dsAnnotation.url()), e);
        }
    }

    /**
     * Converts data from an Excel document to a TestNG {@link DataProvider}. It has a {@link Method} parameter that is automatically
     * injected by TestNG as the current test method.<br/>
     * The test method needs to be annotated with {@link ExcelDataSource} having as attribute, among others, the URL of
     * the Excel document that has to be on the classpath.
     * @param testMethod information about the test method
     * @return a collection of Object arrays defining one or more test scenarios
     */
    @DataProvider(name = "excelDataProvider")
    public static Iterator<Object[]> excelDataProvider(Method testMethod) {
        if (!testMethod.isAnnotationPresent(ExcelDataSource.class)) {
            throw new IllegalStateException(String.format(ERR_MISSING_ANNOT, "@ExcelDataSource", testMethod.getName(), "excelDataProvider"));
        }

        ExcelDataSource dsAnnotation = testMethod.getAnnotation(ExcelDataSource.class);

        try {
            return DataMapper.readMappedExcelRecords(
                    testMethod.getClass().getResource(dsAnnotation.url()),
                    dsAnnotation.format(),
                    dsAnnotation.sheet(),
                    dsAnnotation.hasHeader(),
                    dsAnnotation.arrayElementSeparator(),
                    testMethod.getParameterTypes()).iterator();
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Couldn't load data provider from classpath resource: %s.", dsAnnotation.url()), e);
        }
    }
}
