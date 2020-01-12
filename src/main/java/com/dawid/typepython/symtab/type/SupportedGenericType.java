package com.dawid.typepython.symtab.type;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.operator.MathOperator;
import lombok.AllArgsConstructor;

import java.util.Arrays;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 07.07.2019 at 21:28.
 */
@AllArgsConstructor
public enum SupportedGenericType implements Type {
    TUPLE("tuple", "std::vector"),
    LIST("list", "std::vector"),
    SET("set", "std::set"),
    MAP("map", "std::map"),
    PAIR("pair", "std::pair");

    private final String genericType;
    private final String cppGenericType;


    public static boolean isSupported(String genericType) {
        return Arrays.stream(values()).map(it -> it.genericType).anyMatch(genericType::equals);
    }

    @Override
    public String getCppNameType() {
        return cppGenericType;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public boolean isGenericType() {
        return true;
    }

    @Override
    public boolean isNumeric() {
        return false;
    }


    @Override
    public String getPythonType() {
        return genericType;
    }

    @Override
    public boolean supportOperation(MathOperator mathOperator, Type type) {
        return false;
    }


    public static Type translate(String pythonName, TokenSymbolInfo tokenSymbolInfo) {
        return stream(values())
                .filter(it -> it.genericType.equals(pythonName))
                .findFirst()
                .orElseThrow(() -> new UnsupportedGenericTypeException(pythonName, tokenSymbolInfo));
    }


    @Override
    public MatchType match(Type typedSymbol) {
        if (this == typedSymbol) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }
}
