package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.TypeMatcher;
import com.dawid.typepython.symtab.type.VariableType;
import lombok.Data;

/**
 * Created by Dawid on 07.07.2019 at 21:47.
 */

@Data
public class TypedSymbol extends Symbol implements TypeMatcher {
    protected VariableType variableType;
    private boolean collectionElement = false;

    public TypedSymbol(VariableType variableType) {
        this.variableType = variableType;
    }

    public TypedSymbol(String name) {
        super(name);
    }

    public TypedSymbol(String name, VariableType variableType) {
        super(name);
        this.variableType = variableType;
    }

    public TypedSymbol(SymbolType symbolType, String text) {
        super(symbolType, text);
    }

    public String getTypeName() {
        return variableType.getCppNameType();
    }

    @Override
    public boolean isDeclaredInScope() {
        return collectionElement || super.isDeclaredInScope();
    }

    @Override
    public MatchType match(TypedSymbol t) {
        return variableType.match(t);
    }
}
