package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.type.SymbolType;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    private boolean temporary = false;
    private transient TokenSymbolInfo tokenSymbolInfo;
    protected SymbolType symbolType = SymbolType.TEXT;

    public Symbol(SymbolType symbolType, String text, TokenSymbolInfo tokenSymbolInfo) {
        this.symbolType = symbolType;
        this.displayText = text;
        this.name = text;
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    public Symbol(SymbolType symbolType, Scope scope, TokenSymbolInfo tokenSymbolInfo) {
        this.symbolType = symbolType;
        this.scope = scope;
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    public Symbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        this.name = name;
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    public TokenSymbolInfo getTokenSymbolInfo() {
        return tokenSymbolInfo;
    }

    public String getDisplayText() {
        if (StringUtils.isBlank(displayText)) {
            if (StringUtils.isBlank(name)) {
                return "";
            }
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
