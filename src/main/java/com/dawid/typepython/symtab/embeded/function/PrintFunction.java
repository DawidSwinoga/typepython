package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.matching.MatchType;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 11.09.2019 at 23:19.
 */
public class PrintFunction extends EmbeddedFunction {
    public PrintFunction() {
        super("print", CppVariableType.VOID, new ArrayList<>());
    }

    public String invoke(List<Symbol> parameters) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Symbol symbol : parameters) {
            if (!((TypedSymbol) symbol).getVariableType().isCollection()) {
                stringBuilder.append("cout << " + symbol.getText() + " << endl;");
            } else {
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public MatchType match(TypedSymbol symbol) {
        if (!symbol.getVariableType().isCollection()) {
            return MatchType.FULL;
        } else {
            return MatchType.NONE;
        }
    }
}
