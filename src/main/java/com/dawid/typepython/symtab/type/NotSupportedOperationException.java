package com.dawid.typepython.symtab.type;

import com.dawid.typepython.CompilerException;
import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.operator.MathOperator;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;

/**
 * Created by Dawid on 11.01.2020 at 02:33.
 */
public class NotSupportedOperationException extends CompilerException {
    private final Symbol left;
    private final MathOperator mathOperator;
    private final Symbol right;

    public NotSupportedOperationException(TokenSymbolInfo tokenSymbolInfo, Symbol left, MathOperator mathOperator, Symbol right) {
        super(tokenSymbolInfo);
        this.left = left;
        this.mathOperator = mathOperator;
        this.right = right;
    }

    @Override
    public String getMessage() {
        return "Not supported operation " + left.getName() + " " + mathOperator.getPythonOperator() + " " + right.getName() + getTypeInfo();
    }

    private String getTypeInfo() {
        if (left instanceof TypedSymbol && right instanceof TypedSymbol) {
            return " -> " + ((TypedSymbol)left).getVariableType().getPythonType() + " " + mathOperator.getPythonOperator() + " " + ((TypedSymbol)right).getVariableType().getPythonType();
        }

        return "";
    }
}
