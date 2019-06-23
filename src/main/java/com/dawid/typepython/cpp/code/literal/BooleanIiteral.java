package com.dawid.typepython.cpp.code.literal;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@AllArgsConstructor
@Getter
public enum BooleanIiteral {
    TRUE("True", "true"),
    FALSE("False", "false");

    private String pythonCode;
    private String cppCode;

    public static String translate(String pythonCode) {
        return stream(values())
                .filter(it -> it.pythonCode.equals(pythonCode))
                .findFirst()
                .map(BooleanIiteral::getCppCode)
                .orElseThrow(() -> new UnsupportedLiteralException(pythonCode));
    }
}
