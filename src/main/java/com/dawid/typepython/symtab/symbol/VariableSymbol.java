package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.symbol.type.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableSymbol extends Symbol {
    private Type type;

    public VariableSymbol(Type type) {
        this.type = type;
    }

    public VariableSymbol(String name) {
        super(name);
    }

    public VariableSymbol(String name, Type type) {
        super(name);
        this.type = type;
    }

    public String getTypeName() {
        return type.getNameType();
    }
}
