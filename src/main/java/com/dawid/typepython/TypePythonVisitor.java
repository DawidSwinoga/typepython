package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.literal.BooleanIiteral;
import com.dawid.typepython.cpp.code.operator.CompareOperator;
import com.dawid.typepython.cpp.code.operator.LogicalOperator;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.symtab.GlobalScope;
import com.dawid.typepython.symtab.LocalScope;
import com.dawid.typepython.symtab.Scope;
import com.dawid.typepython.symtab.symbol.CompoundTypedSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import type.CppType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Symbol> {
    private final CodeWriter codeWriter;
    private Scope currentScope;

    public TypePythonVisitor(CodeWriter codeWriter) {
        super();
        this.codeWriter = codeWriter;
    }

    @Override
    public Symbol visitSuite(TypePythonParser.SuiteContext ctx) {
        pushScope(new LocalScope());
        codeWriter.startScope();
        ctx.children.forEach(this::visit);
        codeWriter.endScope();
        popScope();
        return null;
    }

    @Override
    public Symbol visitIfStatement(TypePythonParser.IfStatementContext ctx) {
        codeWriter.write("if (");
        codeWriter.write(visit(ctx.test()).getText());
        codeWriter.write(")");

        visit(ctx.suite());
        ctx.elifStatement().forEach(this::visit);
        Optional.ofNullable(ctx.elseStatement()).ifPresent(this::visit);
        return null;
    }

    @Override
    public Symbol visitElseStatement(TypePythonParser.ElseStatementContext ctx) {
        codeWriter.write("else\n");
        visit(ctx.suite());
        return null;
    }

    @Override
    public Symbol visitElifStatement(TypePythonParser.ElifStatementContext ctx) {
        codeWriter.write("else if(");
        codeWriter.write(visit(ctx.test()).getText());
        codeWriter.write(")\n");
        visit(ctx.suite());
        return null;
    }

    @Override
    public Symbol visitBreakStatement(TypePythonParser.BreakStatementContext ctx) {
        codeWriter.write("break;");
        return null;
    }

    @Override
    public Symbol visitWhileStatement(TypePythonParser.WhileStatementContext ctx) {
        codeWriter.write(" while (");
        codeWriter.write(visit(ctx.test()).getText());
        codeWriter.write(")");
        visit(ctx.suite());
        return null;
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
        if (!assignable.isDeclaredInScope()) {
            currentScope.addVariable(assignable);
        }

        return null;
    }

    @Override
    public Symbol visitIdentifierAtom(TypePythonParser.IdentifierAtomContext ctx) {
        return currentScope.findVariable(ctx.getText()).orElseThrow(() -> new UndefinedVariableException(ctx.getText()));
    }

    @Override
    public Symbol visitIntegerLiteral(TypePythonParser.IntegerLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppType.INT);
    }

    @Override
    public Symbol visitStringLiteral(TypePythonParser.StringLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppType.STRING);
    }

    @Override
    public Symbol visitOrStatement(TypePythonParser.OrStatementContext ctx) {
        Symbol left = visit(ctx.left);
        Symbol operator = new Symbol(LogicalOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.right);
        return CompoundTypedSymbol.of(CppType.BOOLEAN, left, operator, right);
    }

    @Override
    public Symbol visitAndStatement(TypePythonParser.AndStatementContext ctx) {
        Symbol left = visit(ctx.left);
        Symbol operator = new Symbol(LogicalOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.right);
        return CompoundTypedSymbol.of(CppType.BOOLEAN, left, operator, right);
    }

    @Override
    public Symbol visitComparison(TypePythonParser.ComparisonContext ctx) {
        if (!ctx.compareOperator().isEmpty()) {
            List<Symbol> symbols = ctx.children.stream().map(this::visit).collect(Collectors.toList());
            return new CompoundTypedSymbol(symbols, CppType.BOOLEAN);
        }
        return super.visitComparison(ctx);
    }


    @Override
    public Symbol visitCompareOperator(TypePythonParser.CompareOperatorContext ctx) {
        return new Symbol(CompareOperator.translate(ctx.getText()));
    }

    @Override
    public Symbol visitLongLiteral(TypePythonParser.LongLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppType.LONG);
    }

    @Override
    public Symbol visitDoubleLiteral(TypePythonParser.DoubleLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppType.DOUBLE);
    }

    @Override
    public Symbol visitFloatLiteral(TypePythonParser.FloatLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppType.FLOAT);
    }

    @Override
    public Symbol visitBooleanLiteral(TypePythonParser.BooleanLiteralContext ctx) {
        return new VariableSymbol(BooleanIiteral.translate(ctx.getText()), CppType.BOOLEAN);
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

    private void popScope() {
        currentScope = currentScope.getEnclosingScope();
        codeWriter.setScope(currentScope);
    }

}
