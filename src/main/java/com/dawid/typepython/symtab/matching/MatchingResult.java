package com.dawid.typepython.symtab.matching;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import lombok.Value;

import java.util.function.Supplier;

/**
 * Created by Dawid on 11.09.2019 at 12:34.
 */
@Value
public class MatchingResult {
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

    public FunctionSymbol get(MatchType minimumMatchType) {
        return get(minimumMatchType, NoMatchingFunctionException::new);
    }

    public FunctionSymbol minPartial() {
        return get(MatchType.PARTIAL);
    }

    public TypedSymbol fullMatch() {
        return get(MatchType.FULL);
    }
}
