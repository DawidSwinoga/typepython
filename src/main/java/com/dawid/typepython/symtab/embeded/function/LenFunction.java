package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.NoMatchingFunctionExeption;
import com.dawid.typepython.symtab.type.Type;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 12.09.2019 at 01:28.
 */
public class LenFunction extends EmbeddedFunction {
    public LenFunction() {
        super("len", CppVariableType.INT, new ArrayList<>());
    }

    public String invoke(List<Symbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        if (parameters.size() > 1 || parameters.isEmpty() || !((TypedSymbol) parameters.get(0)).getVariableType().isCollection()) {
            throw new NoMatchingFunctionExeption();
        }

        stringBuilder.append(parameters.get(0).getText() + ".size()");

        return stringBuilder.toString();
    }

    @Override
    public MatchType match(TypedSymbol symbol) {
        if (symbol.isCollection()) {
            return MatchType.FULL;
        } else {
            return MatchType.NONE;
        }
    }
}
