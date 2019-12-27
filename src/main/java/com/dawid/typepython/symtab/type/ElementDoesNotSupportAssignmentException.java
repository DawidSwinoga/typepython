package com.dawid.typepython.symtab.type;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;

/**
 * Created by Dawid on 15.10.2019 at 21:00.
 */
public class ElementDoesNotSupportAssignmentException extends CompilerException {
    private TypedSymbol typedSymbol;

    public ElementDoesNotSupportAssignmentException(TypedSymbol typedSymbol) {
        super(typedSymbol.getTokenSymbolInfo());
        this.typedSymbol = typedSymbol;
    }

    @Override
    public String getMessage() {
        if (typedSymbol instanceof FunctionSymbol) {
            return "Function " + typedSymbol.getDisplayText() + " does not support item assignment.";
        }

        return "Object " + typedSymbol.getDisplayText() + " of type " + typedSymbol.getPythonNameType() + " does not support item assignment.";
    }
}
