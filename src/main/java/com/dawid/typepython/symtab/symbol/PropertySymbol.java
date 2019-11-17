package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.type.Type;

import java.util.List;

/**
 * Created by Dawid on 02.11.2019 at 16:43.
 */
public class PropertySymbol extends MethodSymbol {
    public PropertySymbol(String name, Type returnType, List<TypedSymbol> parameters, boolean returnTypeAssignable) {
        super(name, returnType, parameters, returnTypeAssignable, null);
    }

    public PropertySymbol(String name, Type returnType, List<TypedSymbol> parameters) {
        super(name, returnType, parameters, null);
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<TypedSymbol> parameters) {
        String text = getDisplayText();
        return new FunctionResult(text, getVariableType(), returnTypeAssignable);
    }
}
