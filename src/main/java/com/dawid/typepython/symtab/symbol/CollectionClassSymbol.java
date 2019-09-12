package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.matching.MatchType;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import lombok.Getter;

import static java.util.Optional.ofNullable;

/**
 * Created by Dawid on 20.07.2019 at 15:55.
 */
public class CollectionClassSymbol extends ClassSymbol {
    @Getter
    private TypedSymbol nested;

    public CollectionClassSymbol(VariableType variableType) {
        super(variableType);
    }

    public CollectionClassSymbol(String name) {
        super(name);
    }

    public CollectionClassSymbol(VariableType variableType, TypedSymbol nestedSymbol) {
        super(variableType);
        this.nested = nestedSymbol;
    }

    public CollectionClassSymbol(String text, VariableType VariableType, TypedSymbol nestedType) {
        super(text, VariableType);
        this.nested = nestedType;
    }

    @Override
    public String getTypeName() {
        StringBuilder type = new StringBuilder();
        type.append(super.getTypeName());

        ofNullable(nested).map(TypedSymbol::getTypeName).ifPresent(it -> type.append("<").append(it).append(">"));
        return type.toString();
    }

    @Override
    public MatchType match(TypedSymbol typedSymbol) {
        if (isTheSameGenericType(typedSymbol) && checkNestedType(nested, (CollectionClassSymbol) typedSymbol) == MatchType.FULL) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }

    private MatchType checkNestedType(TypedSymbol nested, CollectionClassSymbol typedSymbol) {
        return nested.match(typedSymbol.nested);
    }

    private boolean isTheSameGenericType(TypedSymbol typedSymbol) {
        return super.match(typedSymbol) == MatchType.FULL;
    }
}
