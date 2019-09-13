package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableSymbol extends TypedSymbol {
    public VariableSymbol(Type variableType) {
        super(variableType);
    }

    public VariableSymbol(String name) {
        super(name);
    }

    public VariableSymbol(String name, Type variableType) {
        super(name, variableType);
    }

    public VariableSymbol(SymbolType symbolType, String text) {
        super(symbolType, text);
    }

    @Override
    public SymbolType getSymbolType() {
        return ofNullable(symbolType).orElse(SymbolType.VARIABLE);
    }
}
