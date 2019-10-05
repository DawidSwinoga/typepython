package com.dawid.typepython.symtab.scope;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import com.dawid.typepython.symtab.type.Type;
import lombok.Getter;

import java.util.List;

@Getter
public class FunctionScope extends Scope {
    private List<TypedSymbol> parameters;
    private final Type returnType;

    public FunctionScope(ScopeType scopeType, List<TypedSymbol> parameters, Type returnType) {
        super(scopeType, parameters);
        this.parameters = parameters;
        parameters.forEach(it -> it.setScope(this));
        this.returnType = returnType;
    }

    @Override
    public boolean isFunction() {
        return true;
    }
}
