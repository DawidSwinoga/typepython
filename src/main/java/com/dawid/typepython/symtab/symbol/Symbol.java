package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.Scope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol {
    private String text;
    private Scope scope;

    public Symbol(String name) {
        this.text = name;
    }

    public boolean isDeclaredInScope() {
        return scope != null;
    }
}
