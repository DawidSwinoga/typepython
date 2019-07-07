package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.SymbolType;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableSymbol extends Symbol {
    private VariableType variableType;

    public VariableSymbol(VariableType variableType) {
        this.variableType = variableType;
    }

    public VariableSymbol(String name) {
        super(name);
    }

    public VariableSymbol(String name, VariableType variableType) {
        super(name);
        this.variableType = variableType;
    }

    public String getTypeName() {
        return variableType.getCppNameType();
    }

    @Override
    public SymbolType getSymbolType() {
        return SymbolType.VARIABLE;
    }
}
