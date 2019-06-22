package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.Type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 21.06.2019 at 12:42.
 */
public class CompoundTypedSymbol extends VariableSymbol {
    private List<Symbol> symbols;

    public CompoundTypedSymbol(List<Symbol> symbols, Type type) {
        super(type);
        this.symbols = symbols;
    }

    @Override
    public String getText() {
        return symbols.stream().map(Symbol::getText).collect(Collectors.joining(" "));
    }

    public void addSymbols(List<Symbol> symbols) {
        this.symbols.addAll(symbols);
    }

    public static CompoundTypedSymbol of(Type type, Symbol... symbols) {
        return new CompoundTypedSymbol(Arrays.asList(symbols), type);
    }
}
