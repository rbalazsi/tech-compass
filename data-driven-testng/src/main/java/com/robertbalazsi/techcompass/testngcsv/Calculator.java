package com.robertbalazsi.techcompass.testngcsv;

import java.util.stream.DoubleStream;

/**
 * The subject class, a simple calculator that can perform several types of arithmetic operations having exactly two operands.
 */
public class Calculator {

    public static double calc(double n1, String operation, double n2) {
        switch (operation) {
            case "+":
                return n1 + n2;
            case "-":
                return n1 - n2;
            case "*":
                return n1 * n2;
            case "/":
                return n1 / n2;
            case "%":
                return n1 % n2;
            case "^":
                return Math.pow(n1, n2);
            default:
                throw new UnsupportedOperationException(String.format("Arithmetic operation %s is not supported.", operation));
        }
    }

    public static double arraySum(double[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return 0;
        }

        return DoubleStream.of(numbers).sum();
    }
}
