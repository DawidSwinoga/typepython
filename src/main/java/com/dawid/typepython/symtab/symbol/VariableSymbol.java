package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.SymbolType;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableSymbol extends TypedSymbol {


    public VariableSymbol(VariableType variableType) {
        super(variableType);
    }

    public VariableSymbol(String name) {
        super(name);
    }

    public VariableSymbol(String name, VariableType variableType) {
        super(name, variableType);
    }

    @Override
    public SymbolType getSymbolType() {
        return SymbolType.VARIABLE;
    }
}
