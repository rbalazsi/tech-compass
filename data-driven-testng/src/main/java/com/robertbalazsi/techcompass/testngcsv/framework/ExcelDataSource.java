package com.robertbalazsi.techcompass.testngcsv.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation used on a @Test method to associate an Excel document as data provider to it.
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE, CONSTRUCTOR})
public @interface ExcelDataSource {

    /**
     * The {@link java.net.URL URL} of the Excel document, given as a string. It needs to be a classpath resource relative to the test class.
     */
    String url() default "";

    /**
     * The format of the Excel document: XLSX (default) or the older XLS.
     */
    ExcelFormat format() default ExcelFormat.XLSX;

    /**
     * The name of the worksheet to be used as data provider. If not specified, the first worksheet in the document will be used.
     */
    String sheet() default "";

    /**
     * Indicates whether the first line in the Excel document is considered the header (for easier readability using some tools),
     * and thereby ignored by the test runner.
     */
    boolean hasHeader() default false;

    /**
     * The string to be used as the separator of array elements.
     */
    String arrayElementSeparator() default ",";
}
