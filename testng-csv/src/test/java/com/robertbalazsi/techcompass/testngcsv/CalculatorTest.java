package com.robertbalazsi.techcompass.testngcsv;

import com.robertbalazsi.techcompass.testngcsv.framework.CSVDataSource;
import com.robertbalazsi.techcompass.testngcsv.framework.DataSourceConverters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * The test class for {@link Calculator}.
 */
public class CalculatorTest {
    @Test(dataProvider = "calcDataProvider")
    public void testCalc_hardcodedDataProvider(double n1, String operation, double n2, double expectedResult) {
        assertEquals(Calculator.calc(n1, operation, n2), expectedResult);
    }

    @DataProvider(name = "calcDataProvider")
    public Object[][] calcDataProvider() {
        return new Object[][] {
                {1.0, "+", 7, 8.0},
                {1.0, "-", 7, -6.0},
                {3, "*", 2, 6},
                {8, "/", 2, 4},
                {8, "%", 2, 0},
                {7, "^", 2, 49},
        };
    }

    @Test(dataProviderClass = DataSourceConverters.class, dataProvider = "csvDataProvider")
    @CSVDataSource(url = "/calc_dataProvider.csv", hasHeader = true)
    public void testCalc(double n1, String operation, double n2, double expectedResult) {
        assertEquals(Calculator.calc(n1, operation, n2), expectedResult);
    }

    @Test(dataProviderClass = DataSourceConverters.class, dataProvider = "csvDataProvider")
    @CSVDataSource(url = "/arraySum_dataProvider.csv", hasHeader = true, arrayElementSeparator = ";")
    public void testArraySum(int[] numbers, int expectedSum) {
        assertEquals(Calculator.arraySum(numbers), expectedSum);
    }
}