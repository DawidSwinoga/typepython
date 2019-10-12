package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 21.06.2019 at 12:42.
 */
public class CompoundTypedSymbol extends VariableSymbol {
    @Getter
    //Should be TypedSymbol
    private List<Symbol> symbols;

    public CompoundTypedSymbol(List<Symbol> symbols, Type variableType) {
        super(variableType);
        this.symbols = symbols;
    }

    public CompoundTypedSymbol(List<Symbol> symbols, String name) {
        super(name);
        this.symbols = symbols;
    }

    public CompoundTypedSymbol(List<Symbol> symbols, SymbolType functionCall, String text) {
        super(functionCall, text);
        this.symbols = symbols;
    }

    public static CompoundTypedSymbol of(Type variableType, VariableSymbol first, List<Symbol> symbols) {
        List<Symbol> allSymbols = new ArrayList<>();
        allSymbols.add(first);
        allSymbols.addAll(symbols);
        return new CompoundTypedSymbol(allSymbols, variableType);
    }

    public static CompoundTypedSymbol of(Type variableType, List<Symbol> symbols) {
        return new CompoundTypedSymbol(symbols, variableType);
    }

    public static Symbol of(List<Symbol> symbols, String name) {
        return new CompoundTypedSymbol(symbols, name);
    }

    public static Symbol of(List<Symbol> symbols, SymbolType functionCall, String text) {
        return new CompoundTypedSymbol(symbols, functionCall, text);
    }

    public static Symbol of(Type variableType, String displayText, Symbol... symbols) {
        CompoundTypedSymbol compoundTypedSymbol = new CompoundTypedSymbol(Arrays.asList(symbols), variableType);
        compoundTypedSymbol.setDisplayText(displayText);
        return compoundTypedSymbol;
    }

    public long size() {
        return symbols.size();
    }

    @Override
    public String getDisplayText() {
        String text = super.getDisplayText();
        if (StringUtils.isBlank(text)) {
            return symbols.stream().filter(Objects::nonNull).map(Symbol::getDisplayText).collect(Collectors.joining(" "));
        } else {
            return text;
        }
    }

    public void addSymbols(List<Symbol> symbols) {
        this.symbols.addAll(symbols);
    }

    //TODO remove variableType. Compute variableType from passed symbols
    public static CompoundTypedSymbol of(Type variableType, Symbol... symbols) {
        return new CompoundTypedSymbol(Arrays.asList(symbols), variableType);
    }

    public List<Type> getVariableTypes() {
        return symbols
                .stream()
                .filter(it -> it instanceof TypedSymbol)
                .map(it -> (TypedSymbol)it)
                .map(TypedSymbol::getVariableType)
                .collect(Collectors.toList());
    }
}
