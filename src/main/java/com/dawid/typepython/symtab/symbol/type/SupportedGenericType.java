package com.dawid.typepython.symtab.symbol.type;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.matching.MatchType;
import lombok.AllArgsConstructor;

import java.util.Arrays;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 07.07.2019 at 21:28.
 */
@AllArgsConstructor
public enum SupportedGenericType implements VariableType {
    TUPLE("tuple", "vector"),
    LIST("list", "vector");

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
    public boolean isNumeric() {
        return false;
    }


    @Override
    public String getPythonType() {
        return genericType;
    }


    public static VariableType translate(String pythonName) {
        return stream(values())
                .filter(it -> it.genericType.equals(pythonName))
                .findFirst()
                .orElseThrow(() -> new UnsupportedGenericTypeException(pythonName));
    }


    @Override
    public MatchType match(TypedSymbol typedSymbol) {
        if (this == typedSymbol.getVariableType()) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }
}
