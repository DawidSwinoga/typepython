package com.dawid.typepython.symtab.symbol;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
public class VariableSymbol extends TypedSymbol {
    public VariableSymbol(Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(variableType, tokenSymbolInfo);
    }

    public VariableSymbol(String name, TokenSymbolInfo tokenSymbolInfo) {
        super(name, tokenSymbolInfo);
    }

    public VariableSymbol(String name, Type variableType, TokenSymbolInfo tokenSymbolInfo) {
        super(name, variableType, tokenSymbolInfo);
    }

    public VariableSymbol(SymbolType symbolType, String text, TokenSymbolInfo tokenSymbolInfo) {
        super(symbolType, text, tokenSymbolInfo);
    }

    @Override
    public SymbolType getSymbolType() {
        return ofNullable(symbolType).orElse(SymbolType.VARIABLE);
    }
}
