package com.dawid.typepython.symtab.scope;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import lombok.Getter;

import java.util.List;

@Getter
public class FunctionScope extends Scope {
    private List<TypedSymbol> parameters;
    private final TypedSymbol returnType;

    public FunctionScope(ScopeType scopeType, List<TypedSymbol> parameters, TypedSymbol returnType) {
        super(scopeType, parameters);
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public void addParameters(List<VariableSymbol> parameters) {
        this.parameters.addAll(parameters);
    }

    @Override
    public boolean isFunction() {
        return true;
    }
}
