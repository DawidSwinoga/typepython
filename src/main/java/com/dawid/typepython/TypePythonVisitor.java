package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.symtab.GlobalScope;
import com.dawid.typepython.symtab.Scope;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import type.CppType;

import java.util.Optional;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Symbol> {
    private final CodeWriter codeWriter;
    private Scope currentScope;

    public TypePythonVisitor(CodeWriter codeWriter) {
        super();
        this.codeWriter = codeWriter;
    }

    @Override
    public Symbol visitFileInput(TypePythonParser.FileInputContext ctx) {
        pushScope(new GlobalScope());
        codeWriter.writeStartMain();
        ctx.children.forEach(this::visit);
        codeWriter.writeEndMain();
        return null;
    }

    @Override
    public Symbol visitAssignableExpressionStatement(TypePythonParser.AssignableExpressionStatementContext ctx) {
        VariableSymbol symbol = (VariableSymbol) visit(ctx.test());
        VariableSymbol assignable = (VariableSymbol) visit(ctx.assignable());

        if (symbol.getType() == null) {
            throw new RuntimeException();
        }

        if (assignable.getType() == null) {
            assignable.setType(symbol.getType());
        }
        // TODO check in sym table
        // TODO check type if defined

        codeWriter.writeAssignment(assignable, symbol);
        if (assignable.isDeclaredInScope()) {
            currentScope.addVariable(assignable);
        }

        return null;
    }

    @Override
    public Symbol visitIntegerLiteral(TypePythonParser.IntegerLiteralContext ctx) {
        VariableSymbol variableSymbol = new VariableSymbol(ctx.getText());
        variableSymbol.setType(CppType.INT);
        return variableSymbol;
    }

    @Override
    public Symbol visitAssignableIdentifier(TypePythonParser.AssignableIdentifierContext ctx) {
        Optional<VariableSymbol> variable = currentScope.findVariable(ctx.getText());
        return variable.orElseGet(() -> new VariableSymbol(ctx.getText()));
    }

    private void pushScope(Scope scope) {
        if (currentScope != null) {
            scope.setEnclosingScope(currentScope);
        }
        currentScope = scope;
        codeWriter.setScope(scope);
    }

}
