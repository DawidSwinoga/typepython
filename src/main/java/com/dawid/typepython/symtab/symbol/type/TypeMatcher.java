package com.dawid.typepython.symtab.symbol.type;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.matching.MatchType;

/**
 * Created by Dawid on 11.09.2019 at 00:34.
 */
public interface TypeMatcher {
    MatchType match(TypedSymbol t);
}
