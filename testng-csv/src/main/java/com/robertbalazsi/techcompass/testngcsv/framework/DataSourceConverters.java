package com.robertbalazsi.techcompass.testngcsv.framework;

import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Utility class holding specialized converters from different data formats (like CSV) to data providers. Each converter is
 * specialized on a given format and provides a reusable {@link DataProvider} for test methods to use.
 */
public class DataSourceConverters {

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
            throw new IllegalStateException(String.format("@CSVDataSource annotation is missing from test method: %s. It is needed when used together with the 'csvDataProvider' converter.", testMethod.getName()));
        }

        CSVDataSource dataSourceAnnotation = testMethod.getAnnotation(CSVDataSource.class);

        try {
            return CSVDataMapper.readMappedCSVRecords(
                    testMethod.getClass().getResource(dataSourceAnnotation.url()),
                    dataSourceAnnotation.hasHeader(),
                    dataSourceAnnotation.arrayElementSeparator(),
                    testMethod.getParameterTypes()).iterator();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Couldn't load data provider from classpath resource: %s.", dataSourceAnnotation.url()), e);
        }
    }
}
