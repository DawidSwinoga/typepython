package com.dawid.typepython.cpp.code.operator;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 22.06.2019 at 15:50.
 */

@AllArgsConstructor
@Getter
public enum CompareOperator {
    LT("<", "<"),
    GT(">", ">"),
    EQ("==", "=="),
    LTE("<=", "<="),
    GTE(">=", ">="),
    NEGATION("not", "!"),
    NE("!=", "!=");


    private String pythonOperator;
    private String cppOperator;

    public static String translate(String pythonOperator) {
        return stream(values())
                .filter(it -> it.pythonOperator.equals(pythonOperator))
                .findFirst()
                .map(CompareOperator::getCppOperator)
                .orElseThrow(UnsupportedCompareOperationException::new);
    }
}
