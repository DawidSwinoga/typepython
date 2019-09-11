package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.Scope;
import com.dawid.typepython.symtab.symbol.type.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol implements Serializable {
    private String text;
    private Scope scope;
    protected SymbolType symbolType = SymbolType.TEXT;

    public Symbol(String name) {
        this.text = name;
    }

    public Symbol(SymbolType symbolType, String text) {
        this.symbolType = symbolType;
        this.text = text;
    }

    public boolean isDeclaredInScope() {
        return scope != null;
    }
}
