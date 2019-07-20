package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.VariableType;
import lombok.Data;

/**
 * Created by Dawid on 07.07.2019 at 21:47.
 */

@Data
public class TypedSymbol extends Symbol {
    private VariableType variableType;

    public TypedSymbol(VariableType variableType) {
        this.variableType = variableType;
    }

    public TypedSymbol(String name) {
        super(name);
    }

    public TypedSymbol(String name, VariableType variableType) {
        super(name);
        this.variableType = variableType;
    }

    public String getTypeName() {
        return variableType.getCppNameType();
    }
}
