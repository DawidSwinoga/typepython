package com.dawid.typepython.cpp.code.generator;

/**
 * Created by Dawid on 27.07.2019 at 18:39.
 */
public enum  TemporaryVariableNameGenerator {
    INSTANCE;

    private Long id = 0L;

    public String generateVariableName() {
        return "$tmp" + id++;
    }
}
