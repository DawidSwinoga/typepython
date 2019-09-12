package com.dawid.typepython.symtab.operator;

import lombok.Getter;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 22.06.2019 at 19:31.
 */

@Getter
public enum  LogicalOperator {
    AND("and", "&&"),
    OR("or", "||"),
    NOT("not", "!");

    private final String pythonOperator;
    private final String cppOperator;

    LogicalOperator(String pythonOperator, String cppOperator) {
        this.pythonOperator = pythonOperator;
        this.cppOperator = cppOperator;
    }

    public static String translate(String pythonOperator) {
        return stream(values())
                .filter(it -> it.pythonOperator.equals(pythonOperator))
                .findFirst()
                .map(LogicalOperator::getCppOperator)
                .orElseThrow(UnsupportedLogicalOperator::new);
    }
}
