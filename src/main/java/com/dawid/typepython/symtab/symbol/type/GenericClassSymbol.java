package com.dawid.typepython.symtab.symbol.type;

import com.dawid.typepython.symtab.symbol.CompoundTypedSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import lombok.Getter;

import static java.util.Optional.ofNullable;

/**
 * Created by Dawid on 20.07.2019 at 15:55.
 */
public class GenericClassSymbol extends VariableSymbol {
    @Getter
    private TypedSymbol nested;

    public GenericClassSymbol(VariableType variableType) {
        super(variableType);
    }

    public GenericClassSymbol(String name) {
        super(name);
    }

    public GenericClassSymbol(VariableType variableType, TypedSymbol nestedSymbol) {
        super(variableType);
        this.nested = nestedSymbol;
    }

    public GenericClassSymbol(String text, VariableType VariableType, TypedSymbol typedSymbol) {
        super(text, VariableType);
        this.nested = typedSymbol;
    }

    @Override
    public String getTypeName() {
        StringBuilder type = new StringBuilder();
        type.append(super.getTypeName());

        ofNullable(nested).map(TypedSymbol::getTypeName).ifPresent(it -> type.append("<").append(it).append(">"));
        return type.toString();
    }
}
