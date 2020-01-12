package com.dawid.typepython.symtab.operator;

import lombok.Getter;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 06.07.2019 at 20:02.
 */

@Getter
public enum MathOperator {
    PLUS("+", "+"),
    MINUS("-", "-"),
    MUL("*", "*"),
    DIV("/", "/"),
    MOD("%", "%"),
    POWER("**", "");


    private final String pythonOperator;
    private final String cppOperator;

    MathOperator(String pythonOperator, String cppOperator) {
        this.pythonOperator = pythonOperator;
        this.cppOperator = cppOperator;
    }

    public static String translate(String pythonOperator) {
        return stream(values())
                .filter(it -> it.pythonOperator.equals(pythonOperator))
                .findFirst()
                .map(MathOperator::getCppOperator)
                .orElseThrow(UnsupportedLogicalOperator::new);
    }

    public static MathOperator translateFromPython(String pythonOperator) {
        return stream(values())
                .filter(it -> it.pythonOperator.equals(pythonOperator))
                .findFirst()
                .orElseThrow(UnsupportedLogicalOperator::new);
    }
}
