package com.dawid.typepython.symtab.symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dawid on 21.06.2019 at 12:42.
 */
public class CompoundSymbol extends Symbol {
    private List<Symbol> symbols;

    public CompoundSymbol() {
        this.symbols = new ArrayList<>();
    }


}
