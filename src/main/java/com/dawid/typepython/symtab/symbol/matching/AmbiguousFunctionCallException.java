package com.dawid.typepython.symtab.symbol.matching;

import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Created by Dawid on 25.08.2019 at 02:38.
 */
@AllArgsConstructor
public class AmbiguousFunctionCallException extends RuntimeException {
    List<FunctionSymbol> functionSymbols;
}
