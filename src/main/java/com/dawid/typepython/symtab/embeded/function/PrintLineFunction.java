package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.symbol.TypedSymbol;

import java.util.List;

/**
 * Created by Dawid on 29.11.2019 at 21:31.
 */
public class PrintLineFunction extends PrintFunction {
    public PrintLineFunction() {
        super("println");
    }

    @Override
    protected StringBuilder print(List<TypedSymbol> parameters) {
        StringBuilder result = super.print(parameters);
        return result.append(" << std::endl");
    }
}
