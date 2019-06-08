package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.generated.TypePythonParser;
import org.antlr.symtab.GlobalScope;
import org.antlr.symtab.PrimitiveType;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.VariableSymbol;
import type.Type;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Symbol> {
    private Scope currentScope;

    @Override
    public Symbol visitFileInput(TypePythonParser.FileInputContext ctx) {
        pushScope(new GlobalScope(null));
        CodeWriter.INSTANCE.appendMainCode("int main() {");
        ctx.children.forEach(this::visit);
        CodeWriter.INSTANCE.appendMainCode("return 0; }");
        return null;
    }

    @Override
    public Symbol visitExpressionStatement(TypePythonParser.ExpressionStatementContext ctx) {
        VariableSymbol symbol = (VariableSymbol)visit(ctx.test());
        VariableSymbol assignalbe = (VariableSymbol)visit(ctx.assignable());

        if (symbol.getType() == null) {
            throw new RuntimeException();
        }

        if (assignalbe.getType() == null) {
            assignalbe.setType(symbol.getType());
        }
        // TODO check in sym table
        // TODO check type if defined
        CodeWriter.INSTANCE.appendMainCode(assignalbe.getType().getName() + " " + assignalbe.getName() + " = " + symbol.getName() + ";");

        return null;
    }

    @Override
    public Symbol visitIntegerLiteral(TypePythonParser.IntegerLiteralContext ctx) {
        VariableSymbol variableSymbol = new VariableSymbol(ctx.getText());
        variableSymbol.setType(new PrimitiveType(Type.INT.getName()));
        return variableSymbol;
    }

    @Override
    public Symbol visitAssignableIdentifier(TypePythonParser.AssignableIdentifierContext ctx) {
            //TODO share in symTable
            return new VariableSymbol(ctx.getText());
    }

    private void pushScope(Scope scope) {
        currentScope = scope;
    }

}
