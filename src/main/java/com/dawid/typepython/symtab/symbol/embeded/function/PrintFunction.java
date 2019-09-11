package com.dawid.typepython.symtab.symbol.embeded.function;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.matching.MatchType;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 11.09.2019 at 23:19.
 */
public class PrintFunction extends FunctionSymbol {
    public PrintFunction() {
        super("print", CppVariableType.VOID, new ArrayList<>());
    }

    public String invoke(List<Symbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Symbol symbol : parameters) {
            if (!((TypedSymbol)symbol).getVariableType().isCollection()) {
                stringBuilder.append("cout << " + symbol.getText() + " << endl");
            } else {
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public MatchType match(TypedSymbol t) {
        return MatchType.FULL;
    }
}
