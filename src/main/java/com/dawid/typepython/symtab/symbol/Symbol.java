package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.type.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol implements Serializable {
    private String displayText;
    private String name;
    private Scope scope;
    protected SymbolType symbolType = SymbolType.TEXT;

    public Symbol(String text) {
        this.name = text;
        this.displayText = text;
    }

    public Symbol(SymbolType symbolType, String text) {
        this.symbolType = symbolType;
        this.displayText = text;
        this.name = text;
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

    public Optional<Scope> getScope() {
        return Optional.ofNullable(scope);
    }

    public void setScope(Scope scope) {
        String prefixNamespace = scope.getNamespace().map(it -> it + "::").orElse("");
        displayText = prefixNamespace + getDisplayText();
        this.scope = scope;
    }

    public boolean isDeclaredInScope() {
        return scope != null;
    }
}
