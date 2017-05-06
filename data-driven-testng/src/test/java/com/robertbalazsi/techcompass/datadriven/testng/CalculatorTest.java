package com.robertbalazsi.techcompass.datadriven.testng;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * The test class for {@link Calculator}.
 */
public class CalculatorTest {
    @Test(dataProvider = "calc_hardcodedDataProvider")
    public void testCalcWithHardcodedDataProvider(double n1, String operation, double n2, double expectedResult) {
        assertEquals(Calculator.calc(n1, operation, n2), expectedResult);
    }

    @DataProvider(name = "calc_hardcodedDataProvider")
    public Object[][] calcHardcodedDataProvider() {
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
    @CSVDataSource(url = "/calc_csvDataProvider.csv", hasHeader = true)
    public void testCalcWithCSVDataProvider(double n1, String operation, double n2, double expectedResult) {
        assertEquals(Calculator.calc(n1, operation, n2), expectedResult);
    }

    @Test(dataProviderClass = DataSourceConverters.class, dataProvider = "excelDataProvider")
    @ExcelDataSource(url = "/Calculator_DataProviders.xlsx", sheet = "calc", hasHeader = true)
    public void testCalcWithExcelDataProvider(double n1, String operation, double n2, double expectedResult) {
        assertEquals(Calculator.calc(n1, operation, n2), expectedResult);
    }

    @Test(dataProviderClass = DataSourceConverters.class, dataProvider = "excelDataProvider")
    @ExcelDataSource(url = "/Calculator_DataProviders_oldFormat.xls", format = ExcelFormat.XLS, sheet = "calc", hasHeader = true)
    public void testCalcWithExcelDataProvider_oldXLSFormat(double n1, String operation, double n2, double expectedResult) {
        assertEquals(Calculator.calc(n1, operation, n2), expectedResult);
    }

    @Test(dataProviderClass = DataSourceConverters.class, dataProvider = "csvDataProvider")
    @CSVDataSource(url = "/arraySum_dataProvider.csv", hasHeader = true, arrayElementSeparator = ";")
    public void testArraySumWithCSVDataProvider(double[] numbers, double expectedSum) {
        assertEquals(Calculator.arraySum(numbers), expectedSum);
    }

    @Test(dataProviderClass = DataSourceConverters.class, dataProvider = "excelDataProvider")
    @ExcelDataSource(url = "/Calculator_DataProviders.xlsx", sheet = "arraySum", hasHeader = true, arrayElementSeparator = ";")
    public void testArraySumWithExcelDataProvider(double[] numbers, double expectedSum) {
        assertEquals(Calculator.arraySum(numbers), expectedSum);
    }
}