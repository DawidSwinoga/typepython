package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.type.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol implements Serializable {
    private String displayText;
    private String name;
    private Scope scope;
    protected SymbolType symbolType = SymbolType.TEXT;

    public Symbol(String text) {
        this.displayText = text;
    }

    public Symbol(SymbolType symbolType, String text) {
        this.symbolType = symbolType;
        this.displayText = text;
    }

    public String getDisplayText() {
        if (StringUtils.isBlank(displayText)) {
            return name;
        }

        return displayText;
    }

    public String getName() {
        if (StringUtils.isBlank(name)) {
            return displayText;
        }

        return name;
    }

    public boolean isDeclaredInScope() {
        return scope != null;
    }
}
