package com.dawid.typepython.symtab;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class FunctionScope extends Scope {
    private List<VariableSymbol> parameters;
    private final TypedSymbol returnType;

    public FunctionScope(ScopeType scopeType, List<VariableSymbol> parameters, TypedSymbol returnType) {
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
