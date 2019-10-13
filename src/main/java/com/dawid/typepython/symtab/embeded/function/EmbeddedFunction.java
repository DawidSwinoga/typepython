package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.FunctionType;

import java.util.List;

/**
 * Created by Dawid on 12.09.2019 at 01:39.
 */
public abstract class EmbeddedFunction extends FunctionSymbol {
    public EmbeddedFunction(String name) {
        super(name);
    }

    public EmbeddedFunction(String name, FunctionType returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters);
    }
}
