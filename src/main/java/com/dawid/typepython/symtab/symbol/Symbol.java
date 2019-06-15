package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.Scope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Symbol {
    private String name;
    private Scope scope;

    public Symbol(String name) {
        this.name = name;
    }

    public boolean isDeclaredInScope() {
        return scope != null;
    }
}
