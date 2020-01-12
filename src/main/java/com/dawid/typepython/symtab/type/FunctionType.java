package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.operator.MathOperator;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 13.10.2019 at 12:14.
 */
@EqualsAndHashCode(of = "returnType")
public class FunctionType implements Type {
    private final Type returnType;
    private final List<Type> parameters;

    public FunctionType(Type returnType, List<Type> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public FunctionType(Type returnType) {
        this(returnType, new ArrayList<>());
    }


    @Override
    public String getCppNameType() {
        return returnType.getCppNameType();
    }

    @Override
    public List<MethodSymbol> getMethodSymbol() {
        return returnType.getMethodSymbol();
    }

    @Override
    public boolean supportOperation(MathOperator mathOperator, Type type) {
        return returnType.supportOperation(mathOperator, type);
    }

    @Override
    public boolean isCollection() {
        return returnType.isCollection();
    }

    @Override
    public boolean isNumeric() {
        return returnType.isNumeric();
    }

    @Override
    public String getPythonType() {
        return returnType.getPythonType();
    }

    @Override
    public MatchType match(Type t) {
        return returnType.match(t);
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameters() {
        return parameters;
    }
}
