package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.operator.MathOperator;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 08.06.2019 at 02:47.
 */

@Getter
public enum CppVariableType implements Type {
    INT("int", "int", true),
    LONG("long", "long long", true),
    DOUBLE("double", "double", true),
    FLOAT("float", "float", true),
    BOOLEAN("bool", "bool", true),
    STRING("string", "std::string", false),
    VOID("", "void", false);


    CppVariableType(String pythonName, String cppName, boolean numeric) {
        this.pythonName = pythonName;
        this.cppName = cppName;
        this.numeric = numeric;
    }

    private String cppName;
    private String pythonName;
    private final boolean numeric;

    @Override
    public String getCppNameType() {
        return cppName;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getPythonType() {
        return pythonName;
    }

    @Override
    public List<MethodSymbol> getMethodSymbol() {
        return new ArrayList<>();
    }

    @Override
    public boolean supportOperation(MathOperator mathOperator, Type type) {
        return (type.isNumeric() && this.isNumeric()) || (mathOperator == MathOperator.PLUS && type.match(STRING) != MatchType.NONE);
    }

    public static Optional<? extends Type> translate(String pythonName) {
        return stream(values())
                .filter(it -> it.getPythonName().equals(pythonName))
                .findFirst();
    }

    @Override
    public MatchType match(Type variableType) {
        if (this == variableType) {
            return MatchType.FULL;
        }

        if (isNumeric() && variableType.isNumeric()) {
            return MatchType.PARTIAL;
        }

        return MatchType.NONE;
    }


}
