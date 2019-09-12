package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.symtab.type.collection.CollectionTypeAnalyzer;
import com.dawid.typepython.symtab.literal.BooleanLiteral;
import com.dawid.typepython.symtab.operator.CompareOperator;
import com.dawid.typepython.symtab.operator.LogicalOperator;
import com.dawid.typepython.symtab.operator.MathOperator;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.symtab.scope.FunctionScope;
import com.dawid.typepython.symtab.scope.GlobalScope;
import com.dawid.typepython.symtab.scope.LocalScope;
import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.scope.ScopeType;
import com.dawid.typepython.symtab.symbol.CollectionClassSymbol;
import com.dawid.typepython.symtab.symbol.CompoundTypedSymbol;
import com.dawid.typepython.symtab.symbol.FunctionAlreadyExistException;
import com.dawid.typepython.symtab.symbol.FunctionSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.UndefinedVariableException;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import com.dawid.typepython.symtab.symbol.VariableTypeMissmatchException;
import com.dawid.typepython.symtab.embeded.ListSymbol;
import com.dawid.typepython.symtab.embeded.function.EmbeddedFunction;
import com.dawid.typepython.symtab.embeded.function.LenFunction;
import com.dawid.typepython.symtab.embeded.function.PrintFunction;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.MatchingResult;
import com.dawid.typepython.symtab.matching.NoMatchingFunctionExeption;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.UnsupportedGenericTypeException;
import com.dawid.typepython.symtab.type.VariableType;
import org.antlr.v4.runtime.Token;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dawid.typepython.symtab.type.SupportedGenericType.LIST;
import static java.util.Optional.ofNullable;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Symbol> {
    private final CodeWriter codeWriter;
    private Scope currentScope;
    private List<TypedSymbol> parameters;


    public TypePythonVisitor(CodeWriter codeWriter) {
        super();
        this.codeWriter = codeWriter;
        this.parameters = new ArrayList<>();
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
    public Symbol visitFuncDefinition(TypePythonParser.FuncDefinitionContext ctx) {
        TypedSymbol returnType = ofNullable(ctx.type())
                .map(this::visit)
                .map(it -> (TypedSymbol) it)
                .orElseGet(() -> new TypedSymbol("void", CppVariableType.VOID));
        codeWriter.startFunction();
        codeWriter.write(returnType.getTypeName() + " " + ctx.IDENTIFIER());
        visit(ctx.parameters());
        FunctionSymbol functionSymbol = new FunctionSymbol(ctx.IDENTIFIER().getText(), returnType.getVariableType(), parameters);
        validateFunctionUniqueness(functionSymbol);
        currentScope.addFunctionSymbol(functionSymbol);
        FunctionScope functionScope = new FunctionScope(ScopeType.LOCAL, parameters, returnType);
        pushScope(functionScope);

        validReturnStatement(ctx, functionScope);

        visit(ctx.suite());
        popScope();
        codeWriter.endFunction();
        return null;
    }

    private void validateFunctionUniqueness(FunctionSymbol functionSymbol) {
        MatchingResult result = currentScope.findFunction(functionSymbol.getText(), new ArrayList<>(functionSymbol.getParameters()));
        if (result.getMatchType() == MatchType.FULL) {
            throw new FunctionAlreadyExistException("Function: " + functionSymbol.getText() + " already exist");
        }
    }

    private void validReturnStatement(TypePythonParser.FuncDefinitionContext ctx, FunctionScope functionScope) {
//        Optional<TypePythonParser.ReturnStatementContext> returnStatementContext = ofNullable(ctx.suite())
//                .map(TypePythonParser.SuiteContext::statement)
//                .map(it -> it.get(it.size() - 1))
//                .map(TypePythonParser.StatementContext::simpleStatement)
//                .map(TypePythonParser.SimpleStatementContext::smallStatement)
//                .map(TypePythonParser.SmallStatementContext::flowStatement)
//                .map(TypePythonParser.FlowStatementContext::returnStatement);
//        TypedSymbol returnType = functionScope.getReturnType();
//        if (!returnStatementContext.isPresent() && returnType.getVariableType() != CppVariableType.VOID) {
//            throw new ReturnStatementMissingException();
//        }
    }

    @Override
    public Symbol visitReturnStatement(TypePythonParser.ReturnStatementContext ctx) {
        Symbol returnSymbol = visit(ctx.test());
        FunctionScope functionScope = currentScope.getFunctionScope();
        TypedSymbol returnType = functionScope.getReturnType();
        if (returnType != null) {
            codeWriter.write("return " + returnSymbol.getText() + ";");
        }
        return null;
    }

    @Override
    public Symbol visitParameters(TypePythonParser.ParametersContext ctx) {
        codeWriter.write("(");
        ofNullable(ctx.typeDeclarationArgsList()).ifPresent(this::visit);
        String parametersString = parameters
                .stream()
                .map(it -> it.getTypeName() + " " + it.getText())
                .collect(Collectors.joining(","));
        codeWriter.write(parametersString);
        codeWriter.write(")");
        return null;
    }

    @Override
    public Symbol visitTypeDeclarationArgsList(TypePythonParser.TypeDeclarationArgsListContext ctx) {
        List<TypedSymbol> parameters = ctx.variableDeclaration()
                .stream()
                .map(this::visit)
                .map(it -> (TypedSymbol) it)
                .peek(it -> it.setScope(currentScope))
                .collect(Collectors.toList());
        this.parameters.addAll(parameters);
        return null;
    }

    @Override
    public Symbol visitIfStatement(TypePythonParser.IfStatementContext ctx) {
        codeWriter.write("if (");
        codeWriter.write(visit(ctx.test()).getText());
        codeWriter.write(")");

        visit(ctx.suite());
        ctx.elifStatement().forEach(this::visit);
        ofNullable(ctx.elseStatement()).ifPresent(this::visit);
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

    //TODO use reference instead of value (for (int $fd : list))
    @Override
    public Symbol visitForStatement(TypePythonParser.ForStatementContext ctx) {
        TypedSymbol variableSymbol = (TypedSymbol) visit(ctx.collection);
        String collectionVariableName = variableSymbol.getText();

        TypedSymbol collection;
        if (variableSymbol.isDeclaredInScope()) {
            collection = currentScope.findAtom(collectionVariableName)
                    .orElseThrow(() -> new UndefinedVariableException(collectionVariableName));
        } else {
            collection = variableSymbol;
            collection.setText(collection.getTypeName() + collection.getText());
        }

        if (!collection.getVariableType().isCollection()) {
            throw new VariableTypeMissmatchException(collectionVariableName + ": " + collection.getTypeName() + ". Expected collection type");
        }

        CollectionClassSymbol genericCollection = (CollectionClassSymbol) collection;
        TypedSymbol nested = genericCollection.getNested();
        nested.setText(ctx.variable.getText());

        pushScope(new LocalScope());
        currentScope.addVariable(nested);
        codeWriter.write("for (" + nested.getTypeName() + " " + nested.getText() +
                " : " + genericCollection.getText() + ")");
        visit(ctx.suite());

        popScope();
        return null;
    }

    @Override
    public Symbol visitAdditiveExpression(TypePythonParser.AdditiveExpressionContext ctx) {
        TypedSymbol left = (TypedSymbol) visit(ctx.expr());
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.term());
        return CompoundTypedSymbol.of(left.getVariableType(), left, mathOperator, right);
    }

    @Override
    public Symbol visitMultiplicativeExpression(TypePythonParser.MultiplicativeExpressionContext ctx) {
        TypedSymbol left = (TypedSymbol) visit(ctx.term());
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()));
        Symbol right = visit(ctx.factor());
        return CompoundTypedSymbol.of(left.getVariableType(), left, mathOperator, right);
    }

    @Override
    public Symbol visitSignFactor(TypePythonParser.SignFactorContext ctx) {
        Optional<Symbol> sign = ofNullable(ctx.sign).map(Token::getText).map(MathOperator::translate).map(Symbol::new);
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
            return new ListSymbol(LIST);
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

        if (variableSymbol instanceof CollectionClassSymbol) {
            detectNested(symbols, (CollectionClassSymbol) variableSymbol);
        }

        return new ListSymbol("{" + symbolText + "}", LIST, variableSymbol);
    }

    private void detectNested(List<? extends TypedSymbol> symbols, CollectionClassSymbol variableSymbol) {
        List<TypedSymbol> nestedSymbols = symbols.stream().map(it -> ((CollectionClassSymbol) it).getNested()).collect(Collectors.toList());
        TypedSymbol nested = variableSymbol.getNested();
        nested.setVariableType(CollectionTypeAnalyzer.detectNestedType(nestedSymbols));
        if (nested instanceof CollectionClassSymbol) {
            detectNested(nestedSymbols, (CollectionClassSymbol) nested);
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
        Symbol symbol = super.visit(ctx.atom());
        List<TypePythonParser.TrailerContext> trailers = ctx.trailer();

        return getAtom(symbol, trailers);
    }

    private Symbol getAtom(Symbol symbol, List<TypePythonParser.TrailerContext> trailers) {
        if (CollectionUtils.isNotEmpty(trailers)) {
            return handleTrailerSymbols(trailers, symbol);
        }

        return symbol;
    }

    @Override
    public Symbol visitAtomTrailer(TypePythonParser.AtomTrailerContext ctx) {
        Symbol symbol = super.visit(ctx.atom());
        List<TypePythonParser.TrailerContext> trailers = ctx.trailer();

        return getAtom(symbol, trailers);
    }

    public Symbol handleTrailerSymbols(List<TypePythonParser.TrailerContext> trailers, Symbol atom) {
        List<Symbol> trailerSymbols = trailers.stream().map(this::visit).collect(Collectors.toList());

        TypedSymbol resultSymbol = (TypedSymbol) atom;

        for (Symbol trailerSymbol : trailerSymbols) {
            if (trailerSymbol.getSymbolType() == SymbolType.FUNCTION_CALL) {

                CompoundTypedSymbol compoundTypedSymbol = (CompoundTypedSymbol) trailerSymbol;

                if (resultSymbol instanceof EmbeddedFunction) {
                    EmbeddedFunction embeddedFunction = (EmbeddedFunction) resultSymbol;
                    return new TypedSymbol(embeddedFunction.invoke(compoundTypedSymbol.getSymbols()), embeddedFunction.getVariableType());
                }

                MatchingResult resultFunctionMatching = currentScope.findFunction(atom.getText(), compoundTypedSymbol.getSymbols());
                if (resultFunctionMatching.getMatchType() == MatchType.NONE) {
                    throw new NoMatchingFunctionExeption();
                }
                FunctionSymbol function = resultFunctionMatching.getFunctionSymbol();
                //TODO add handling multiple trailers call
                resultSymbol = SerializationUtils.clone(function);
                resultSymbol.setText(function.getText() + trailerSymbol.getText());
            }

            if (trailerSymbol.getSymbolType() == SymbolType.GET_COLLECTION_ELEMENT) {
                if (resultSymbol.getVariableType() != LIST) {
                    throw new NoMatchingFunctionExeption();
                }
                Symbol index = ((CompoundTypedSymbol) trailerSymbol).getSymbols().get(0);
                TypedSymbol element = SerializationUtils.clone(((ListSymbol) resultSymbol).getElement((TypedSymbol) index));
                element.setText(resultSymbol.getText() + trailerSymbol.getText());
                element.setCollectionElement(true);
                resultSymbol = element;
            }
        }

        return resultSymbol;
    }


    @Override
    public Symbol visitTrailerParenthesis(TypePythonParser.TrailerParenthesisContext ctx) {
        TypePythonParser.ArgumentsContext argumentsSymbol = ctx.arguments();
        if (argumentsSymbol != null) {
            List<Symbol> arguments = argumentsSymbol.children.stream().map(this::visit).filter(Objects::nonNull).collect(Collectors.toList());
            String text = arguments
                    .stream()
                    .map(it -> (TypedSymbol) it)
                    .map(this::getTrailerText)
                    .collect(Collectors.joining(","));
            return CompoundTypedSymbol.of(arguments, SymbolType.FUNCTION_CALL, "(" + text + ")");
        }
        return null;
    }

    @Override
    public Symbol visitTrailerBrackets(TypePythonParser.TrailerBracketsContext ctx) {
        TypedSymbol typedSymbol = (TypedSymbol) visit(ctx.argument());
        Symbol symbol = CompoundTypedSymbol.of(typedSymbol.getVariableType(), Collections.singletonList(typedSymbol));
        symbol.setSymbolType(SymbolType.GET_COLLECTION_ELEMENT);
        symbol.setText("[" + symbol.getText() + "]");
        return symbol;
    }

    private String getTrailerText(TypedSymbol symbol) {
        if (symbol instanceof CollectionClassSymbol && !symbol.isDeclaredInScope()) {
            return symbol.getTypeName() + symbol.getText();
        }
        return symbol.getText();
    }

    @Override
    public Symbol visitGenericType(TypePythonParser.GenericTypeContext ctx) {
        String genericType = ctx.IDENTIFIER().getText();
        if (!SupportedGenericType.isSupported(genericType)) {
            throw new UnsupportedGenericTypeException(genericType);
        }

        TypedSymbol visit = (TypedSymbol) visit(ctx.type());

        VariableType variableType = SupportedGenericType.translate(genericType);
        if (variableType == LIST) {
            return new ListSymbol(variableType, visit);
        }
        return new CollectionClassSymbol(variableType, visit);
    }

    @Override
    public Symbol visitFileInput(TypePythonParser.FileInputContext ctx) {
        pushScope(new GlobalScope());
        currentScope.addFunctionSymbol(new PrintFunction());
        currentScope.addFunctionSymbol(new LenFunction());
        codeWriter.writeStartMain();
        ctx.children.forEach(this::visit);
        codeWriter.writeEndMain();
        return null;
    }

    @Override
    public Symbol visitExecuteStatement(TypePythonParser.ExecuteStatementContext ctx) {
        Symbol atom = visit(ctx.atom());
        Symbol symbol = handleTrailerSymbols(ctx.trailer(), atom);
        codeWriter.write(symbol.getText() + ";");
        return null;
    }

    //TODO user reference vector<int> &test = ddd; ??
    @Override
    public Symbol visitAssignableExpressionStatement(TypePythonParser.AssignableExpressionStatementContext ctx) {
        TypedSymbol assignable = (TypedSymbol) visit(ctx.assignable());
        TypedSymbol symbol = (TypedSymbol) visit(ctx.test());

        if (symbol.getVariableType() == null) {
            throw new RuntimeException();
        }

        if (assignable.getVariableType() == null) {
            if (symbol instanceof ListSymbol) {
                String text = assignable.getText();
                assignable = new ListSymbol(symbol.getVariableType(), ((CollectionClassSymbol) symbol).getNested());
                assignable.setText(text);
            } else {
                assignable.setVariableType(symbol.getVariableType());
            }
        } else {
            MatchType match = symbol.match(assignable);
            if (match == MatchType.NONE) {
                throw new VariableTypeMissmatchException();
            }
        }

        codeWriter.writeAssignment(assignable, symbol);
        if (!assignable.isDeclaredInScope()) {
            currentScope.addVariable(assignable);
        }

        return null;
    }

    @Override
    public Symbol visitIdentifierAtom(TypePythonParser.IdentifierAtomContext ctx) {
        return currentScope.findAtom(ctx.getText()).orElseThrow(() -> new UndefinedVariableException(ctx.getText()));
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
        Optional<TypedSymbol> variable = currentScope.findAtom(ctx.getText());
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
        parameters = new ArrayList<>();
    }

}
