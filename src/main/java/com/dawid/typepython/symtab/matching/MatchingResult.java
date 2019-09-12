package com.dawid.typepython.symtab.matching;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import lombok.Value;

/**
 * Created by Dawid on 11.09.2019 at 12:34.
 */
@Value
public class MatchingResult {
    private FunctionSymbol functionSymbol;
    private MatchType matchType;
}
