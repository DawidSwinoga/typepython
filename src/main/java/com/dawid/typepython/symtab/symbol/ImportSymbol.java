package com.dawid.typepython.symtab.symbol;

import java.util.List;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.MatchingResult;
import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;

/**
 * Created by Dawid on 06.10.2019 at 20:53.
 */
public class ImportSymbol extends TypedSymbol {
    public ImportSymbol(SymbolType symbolType, Scope scope) {
        super(symbolType, scope, null);
    }

    @Override
    public MatchingResult findMethod(String methodName, List<Type> parameters, TokenSymbolInfo tokenSymbolInfo) {
        return getScope().map(it -> it.findFunction(methodName, parameters, tokenSymbolInfo)).orElse(new MatchingResult(methodName, parameters, null, MatchType.NONE));
    }
}
