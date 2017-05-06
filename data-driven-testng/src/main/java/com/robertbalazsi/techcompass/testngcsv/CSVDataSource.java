package com.robertbalazsi.techcompass.testngcsv;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotation used on a @Test method to associate a CSV resource as data provider to it.
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE, CONSTRUCTOR})
public @interface CSVDataSource {

    /**
     * The {@link java.net.URL URL} of the CSV, given as a string. It needs to be a classpath resource relative to the test class.
     */
    String url() default "";

    /**
     * Indicates whether the first line in the CSV is considered the header (for easier readability using some tools),
     * and thereby ignored by the test runner.
     */
    boolean hasHeader() default false;

    /**
     * The string to be used as the separator of array elements.
     */
    String arrayElementSeparator() default ",";
}
