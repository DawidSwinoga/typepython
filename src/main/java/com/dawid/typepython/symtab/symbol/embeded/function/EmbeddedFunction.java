package com.dawid.typepython.symtab.symbol.embeded.function;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.type.VariableType;

import java.util.List;

/**
 * Created by Dawid on 12.09.2019 at 01:39.
 */
public abstract class EmbeddedFunction extends FunctionSymbol {
    public EmbeddedFunction(String name, VariableType returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
    }

    public abstract String invoke(List<Symbol> parameters);
}
