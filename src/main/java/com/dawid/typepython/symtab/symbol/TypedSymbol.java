package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Dawid on 07.07.2019 at 21:47.
 */

@Setter
public class TypedSymbol extends Symbol {
    @Getter
    protected Type variableType;
    @Getter
    private boolean collectionElement = false;

    public TypedSymbol(Type variableType) {
        this.variableType = variableType;
    }

    public TypedSymbol(String name) {
        super(name);
    }

    public TypedSymbol(String name, Type variableType) {
        super(name);
        this.variableType = variableType;
    }

    public TypedSymbol(SymbolType symbolType, String text) {
        super(symbolType, text);
    }

    public String getCppNameType() {
        return variableType.getCppNameType();
    }

    @Override
    public boolean isDeclaredInScope() {
        return collectionElement || super.isDeclaredInScope();
    }

    public MatchType match(TypedSymbol t) {
        return variableType.match(t.getVariableType());
    }

    public boolean isCollection() {
        return variableType.isCollection();
    }
}
