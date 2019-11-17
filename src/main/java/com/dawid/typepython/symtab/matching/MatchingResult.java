package com.dawid.typepython.symtab.matching;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.Type;
import lombok.Value;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Dawid on 11.09.2019 at 12:34.
 */
@Value
public class MatchingResult {
    private String name;
    private List<Type> parameters;
    private FunctionSymbol functionSymbol;
    private MatchType matchType;

    public FunctionSymbol get(MatchType minMatchType, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (minMatchType == MatchType.FULL && this.matchType != MatchType.FULL) {
            throw exceptionSupplier.get();
        }

        if (minMatchType == MatchType.PARTIAL && this.matchType == MatchType.NONE) {
            throw exceptionSupplier.get();
        }

        return functionSymbol;
    }

    public FunctionSymbol get(MatchType minimumMatchType, TokenSymbolInfo tokenSymbolInfo) {
        return get(minimumMatchType, () -> new NoMatchingFunctionException(name, parameters, tokenSymbolInfo));
    }

    public FunctionSymbol minPartial(TokenSymbolInfo tokenSymbolInfo) {
        return get(MatchType.PARTIAL, tokenSymbolInfo);
    }

    public TypedSymbol fullMatch(TokenSymbolInfo tokenSymbolInfo) {
        return get(MatchType.FULL, tokenSymbolInfo);
    }
}
