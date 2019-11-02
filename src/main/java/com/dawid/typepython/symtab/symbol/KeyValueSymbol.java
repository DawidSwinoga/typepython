package com.dawid.typepython.symtab.symbol;

import lombok.Value;

/**
 * Created by Dawid on 02.11.2019 at 00:37.
 */

@Value
public class KeyValueSymbol extends Symbol{
    private TypedSymbol key;
    private TypedSymbol value;

    @Override
    public String getDisplayText() {
        return "{" + key.getDisplayText() + "," + value.getDisplayText() + "}";
    }
}
