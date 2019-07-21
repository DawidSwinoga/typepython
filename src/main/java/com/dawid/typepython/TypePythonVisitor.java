package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.collection.CollectionTypeAnalyzer;
import com.dawid.typepython.cpp.code.literal.BooleanLiteral;
import com.dawid.typepython.cpp.code.operator.CompareOperator;
import com.dawid.typepython.cpp.code.operator.LogicalOperator;
import com.dawid.typepython.cpp.code.operator.MathOperator;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.symtab.GlobalScope;
import com.dawid.typepython.symtab.LocalScope;
import com.dawid.typepython.symtab.Scope;
import com.dawid.typepython.symtab.symbol.CompoundTypedSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import com.dawid.typepython.symtab.symbol.type.GenericClassSymbol;
import com.dawid.typepython.symtab.symbol.type.SupportedGenericType;
import com.dawid.typepython.symtab.symbol.type.UnsupportedGenericTypeException;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.antlr.v4.runtime.Token;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dawid.typepython.symtab.symbol.type.SupportedGenericType.LIST;

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

    //TODO check in compound symbol the best variableType for result ex. 2 + 1.0 should be double
    @Override
    public Symbol visitAdditiveExpression(TypePythonParser.AdditiveExpressionContext ctx) {
        VariableSymbol left = (VariableSymbol) visit(ctx.expr());
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.term());
        return CompoundTypedSymbol.of(left.getVariableType(), left, mathOperator, right);
    }

    @Override
    public Symbol visitMultiplicativeExpression(TypePythonParser.MultiplicativeExpressionContext ctx) {
        VariableSymbol left = (VariableSymbol) visit(ctx.term());
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.factor());
        return CompoundTypedSymbol.of(left.getVariableType(), left, mathOperator, right);
    }

    @Override
    public Symbol visitSignFactor(TypePythonParser.SignFactorContext ctx) {
        Optional<Symbol> sign = Optional.ofNullable(ctx.sign).map(Token::getText).map(MathOperator::translate).map(Symbol::new);
        VariableSymbol visit = (VariableSymbol) visit(ctx.factor());

        return sign.map(symbol -> (Symbol) CompoundTypedSymbol.of(visit.getVariableType(), symbol, visit)).orElse(visit);
    }

    @Override
    public Symbol visitNegationTest(TypePythonParser.NegationTestContext ctx) {
        return CompoundTypedSymbol.of(CppVariableType.BOOLEAN, new Symbol(LogicalOperator.NOT.getCppOperator()), visit(ctx.notTest()));
    }

    @Override
    public Symbol visitConditionalTupleAtom(TypePythonParser.ConditionalTupleAtomContext ctx) {
        if (ctx.arguments() != null) {
            CompoundTypedSymbol visit = (CompoundTypedSymbol) visit(ctx.arguments());

            if (visit.size() == 1) {
                return CompoundTypedSymbol.of(visit.getVariableType(), new Symbol("("), visit, new Symbol(")"));
            } else {
                throw new UnsupportedOperationException();
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public Symbol visitVariableDeclaration(TypePythonParser.VariableDeclarationContext ctx) {
        TypedSymbol symbol = (TypedSymbol) visit(ctx.type());
        symbol.setText(ctx.IDENTIFIER().getText());
        return symbol;
    }

    @Override
    public Symbol visitSimpleType(TypePythonParser.SimpleTypeContext ctx) {
        return new TypedSymbol(ctx.getText(), CppVariableType.translate(ctx.getText()));
    }

    @Override
    public Symbol visitArguments(TypePythonParser.ArgumentsContext ctx) {
        VariableSymbol first = (VariableSymbol) visit(ctx.first);
        if (ctx.argument().size() == 1) {
            return CompoundTypedSymbol.of(first.getVariableType(), first);
        }

        List<Symbol> symbols = ctx.argument().stream().map(this::visit).collect(Collectors.toList());
        return CompoundTypedSymbol.of(first.getVariableType(), first, symbols);
    }

    @Override
    public Symbol visitListAtom(TypePythonParser.ListAtomContext ctx) {
        if (ctx.arguments().isEmpty()) {
            return new GenericClassSymbol(LIST);
        }

        List<VariableSymbol> symbols = ctx.arguments().argument()
                .stream()
                .map(this::visit)
                .map(it -> (VariableSymbol) it)
                .collect(Collectors.toList());

        String symbolText = symbols.stream().map(Symbol::getText).collect(Collectors.joining(","));
        VariableType variableType = CollectionTypeAnalyzer.detectNestedType(new ArrayList<>(symbols));
        VariableSymbol variableSymbol = symbols.get(0);
        variableSymbol.setText(null);
        variableSymbol.setVariableType(variableType);

        if (variableSymbol instanceof GenericClassSymbol) {
            detectNested(symbols, (GenericClassSymbol) variableSymbol);
        }

        return new GenericClassSymbol("{" + symbolText + "}", LIST, variableSymbol);
    }

    private void detectNested(List<? extends TypedSymbol> symbols, GenericClassSymbol variableSymbol) {
        List<TypedSymbol> nestedSymbols = symbols.stream().map(it -> ((GenericClassSymbol) it).getNested()).collect(Collectors.toList());
        TypedSymbol nested = variableSymbol.getNested();
        nested.setVariableType(CollectionTypeAnalyzer.detectNestedType(nestedSymbols));
        if (nested instanceof GenericClassSymbol) {
            detectNested(nestedSymbols, (GenericClassSymbol) nested);
        }
    }

    @Override
    public Symbol visitConditionalPower(TypePythonParser.ConditionalPowerContext ctx) {
        Symbol atom = visit(ctx.atomExpression());

        if (ctx.exponent != null) {
            Symbol factor = visit(ctx.factor());
            return CompoundTypedSymbol.of(CppVariableType.DOUBLE, new Symbol("pow("), atom,
                    new Symbol(","), factor, new Symbol(")"));
        }

        return atom;
    }

    @Override
    public Symbol visitAtomExpression(TypePythonParser.AtomExpressionContext ctx) {
        return super.visitAtomExpression(ctx);
    }

    @Override
    public Symbol visitGenericType(TypePythonParser.GenericTypeContext ctx) {
        String genericType = ctx.IDENTIFIER().getText();
        if (!SupportedGenericType.isSupported(genericType)) {
            throw new UnsupportedGenericTypeException(genericType);
        }

        TypedSymbol visit = (TypedSymbol) visit(ctx.type());

        return new GenericClassSymbol(SupportedGenericType.translate(genericType), visit);
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
        VariableSymbol assignable = (VariableSymbol) visit(ctx.assignable());
        VariableSymbol symbol = (VariableSymbol) visit(ctx.test());

        if (symbol.getVariableType() == null) {
            throw new RuntimeException();
        }

        if (assignable.getVariableType() == null) {
            if (symbol instanceof GenericClassSymbol) {
                String text = assignable.getText();
                assignable = new GenericClassSymbol(symbol.getVariableType(), ((GenericClassSymbol) symbol).getNested());
                assignable.setText(text);
            } else {
                assignable.setVariableType(symbol.getVariableType());
            }
        }
        // TODO check in sym table
        // TODO check variableType if defined

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
        return new VariableSymbol(ctx.getText(), CppVariableType.INT);
    }

    @Override
    public Symbol visitStringLiteral(TypePythonParser.StringLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.STRING);
    }

    @Override
    public Symbol visitOrStatement(TypePythonParser.OrStatementContext ctx) {
        Symbol left = visit(ctx.left);
        Symbol operator = new Symbol(LogicalOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.right);
        return CompoundTypedSymbol.of(CppVariableType.BOOLEAN, left, operator, right);
    }

    @Override
    public Symbol visitAndStatement(TypePythonParser.AndStatementContext ctx) {
        Symbol left = visit(ctx.left);
        Symbol operator = new Symbol(LogicalOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.right);
        return CompoundTypedSymbol.of(CppVariableType.BOOLEAN, left, operator, right);
    }

    @Override
    public Symbol visitComparison(TypePythonParser.ComparisonContext ctx) {
        if (!ctx.compareOperator().isEmpty()) {
            List<Symbol> symbols = ctx.children.stream().map(this::visit).collect(Collectors.toList());
            return new CompoundTypedSymbol(symbols, CppVariableType.BOOLEAN);
        }
        return super.visitComparison(ctx);
    }


    @Override
    public Symbol visitCompareOperator(TypePythonParser.CompareOperatorContext ctx) {
        return new Symbol(CompareOperator.translate(ctx.getText()));
    }

    @Override
    public Symbol visitLongLiteral(TypePythonParser.LongLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.LONG);
    }

    @Override
    public Symbol visitDoubleLiteral(TypePythonParser.DoubleLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.DOUBLE);
    }

    @Override
    public Symbol visitFloatLiteral(TypePythonParser.FloatLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.FLOAT);
    }

    @Override
    public Symbol visitBooleanLiteral(TypePythonParser.BooleanLiteralContext ctx) {
        return new VariableSymbol(BooleanLiteral.translate(ctx.getText()), CppVariableType.BOOLEAN);
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
