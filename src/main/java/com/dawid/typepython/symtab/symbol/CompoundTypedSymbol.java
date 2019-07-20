package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.VariableType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 21.06.2019 at 12:42.
 */
public class CompoundTypedSymbol extends VariableSymbol {
    private List<Symbol> symbols;

    public CompoundTypedSymbol(List<Symbol> symbols, VariableType variableType) {
        super(variableType);
        this.symbols = symbols;
    }

    public static CompoundTypedSymbol of(VariableType variableType, VariableSymbol first, List<Symbol> symbols) {
        List<Symbol> allSymbols = new ArrayList<>();
        allSymbols.add(first);
        allSymbols.addAll(symbols);
        return new CompoundTypedSymbol(allSymbols, variableType);
    }

    public long size() {
        return symbols.size();
    }

    @Override
    public String getText() {
        return symbols.stream().filter(Objects::nonNull).map(Symbol::getText).collect(Collectors.joining(" "));
    }

    public void addSymbols(List<Symbol> symbols) {
        this.symbols.addAll(symbols);
    }

    //TODO remove variableType. Compute variableType from passed symbols
    public static CompoundTypedSymbol of(VariableType variableType, Symbol... symbols) {
        return new CompoundTypedSymbol(Arrays.asList(symbols), variableType);
    }
}
