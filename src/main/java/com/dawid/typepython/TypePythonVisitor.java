package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.LibraryConsoleCodeWriter;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.embeded.function.FilterFunction;
import com.dawid.typepython.symtab.embeded.function.LenFunction;
import com.dawid.typepython.symtab.embeded.function.MapFunction;
import com.dawid.typepython.symtab.embeded.function.PrintFunction;
import com.dawid.typepython.symtab.embeded.list.ListSymbol;
import com.dawid.typepython.symtab.embeded.list.ListSymbolFactory;
import com.dawid.typepython.symtab.embeded.vector.TupleSymbolFactory;
import com.dawid.typepython.symtab.literal.BooleanLiteral;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.MatchingResult;
import com.dawid.typepython.symtab.matching.NoMatchingFunctionException;
import com.dawid.typepython.symtab.operator.CompareOperator;
import com.dawid.typepython.symtab.operator.LogicalOperator;
import com.dawid.typepython.symtab.operator.MathOperator;
import com.dawid.typepython.symtab.scope.FunctionScope;
import com.dawid.typepython.symtab.scope.GlobalScope;
import com.dawid.typepython.symtab.scope.ImportScope;
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
import com.dawid.typepython.symtab.type.ElementDoesNotSupportAssignmentException;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.SymbolType;
import com.dawid.typepython.symtab.type.Type;
import com.dawid.typepython.symtab.type.TypeNotDefinedException;
import com.dawid.typepython.symtab.type.UnsupportedGenericTypeException;
import com.dawid.typepython.symtab.type.collection.TypeAnalyzer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dawid.typepython.symtab.type.SupportedGenericType.LIST;
import static java.util.Optional.ofNullable;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Symbol> {
    private final CodeWriter codeWriter;
    private Scope currentScope;
    private List<TypedSymbol> parameters;


    public TypePythonVisitor(CodeWriter codeWriter, GlobalScope scope) {
        super();
        this.codeWriter = codeWriter;
        this.parameters = new ArrayList<>();
        pushScope(scope);
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
        codeWriter.writeFunctionDeclaration(returnType.getCppNameType(), ctx.IDENTIFIER().getText());
        visit(ctx.parameters());
        List<Type> parametersTypes = parameters.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList());
        FunctionSymbol functionSymbol = new FunctionSymbol(ctx.IDENTIFIER().getText(), new FunctionType(returnType.getVariableType(), parametersTypes), parameters);
        functionSymbol.setScope(currentScope);
        validateFunctionUniqueness(functionSymbol);
        currentScope.addFunctionSymbol(functionSymbol);
        FunctionScope functionScope = new FunctionScope(ScopeType.LOCAL, parameters, returnType.getVariableType());
        pushScope(functionScope);

        validReturnStatement(ctx, functionScope);

        visit(ctx.suite());
        popScope();
        codeWriter.endFunction();
        return null;
    }

    private void validateFunctionUniqueness(FunctionSymbol functionSymbol) {
        MatchingResult result = currentScope.findFunction(functionSymbol.getDisplayText(), functionSymbol.getParameterTypes());
        if (result.getMatchType() == MatchType.FULL) {
            throw new FunctionAlreadyExistException("Function: " + functionSymbol.getDisplayText() + " already exist");
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
        Type returnType = functionScope.getReturnType();
        if (returnType != null) {
            codeWriter.write("return " + returnSymbol.getDisplayText() + ";");
        }
        return null;
    }

    @Override
    public Symbol visitParameters(TypePythonParser.ParametersContext ctx) {
        ofNullable(ctx.typeDeclarationArgsList()).ifPresent(this::visit);
        String parametersString = parameters
                .stream()
                .map(it -> it.getCppNameType() + " " + it.getDisplayText())
                .collect(Collectors.joining(","));
        codeWriter.writeFunctionParameters("(" + parametersString + ")");
        return null;
    }

    @Override
    public Symbol visitTypeDeclarationArgsList(TypePythonParser.TypeDeclarationArgsListContext ctx) {
        List<TypedSymbol> parameters = ctx.variableDeclaration()
                .stream()
                .map(this::visit)
                .map(it -> (TypedSymbol) it)
                .collect(Collectors.toList());
        this.parameters.addAll(parameters);
        return null;
    }

    @Override
    public Symbol visitIfStatement(TypePythonParser.IfStatementContext ctx) {
        codeWriter.write("if (");
        codeWriter.write(visit(ctx.test()).getDisplayText());
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
        codeWriter.write(visit(ctx.test()).getDisplayText());
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
        codeWriter.write(visit(ctx.test()).getDisplayText());
        codeWriter.write(")");
        visit(ctx.suite());
        return null;
    }

    //TODO use reference instead of value (for (int $fd : list))
    @Override
    public Symbol visitForStatement(TypePythonParser.ForStatementContext ctx) {
        TypedSymbol variableSymbol = (TypedSymbol) visit(ctx.collection);
        String collectionVariableName = variableSymbol.getDisplayText();

        TypedSymbol collection;
        if (variableSymbol.isDeclaredInScope()) {
            collection = currentScope.findAtom(collectionVariableName)
                    .orElseThrow(() -> new UndefinedVariableException(collectionVariableName));
        } else {
            collection = variableSymbol;
            collection.setDisplayText(collection.getCppNameType() + collection.getDisplayText());
        }

        if (!collection.getVariableType().isCollection()) {
            throw new VariableTypeMissmatchException(collectionVariableName + ": " + collection.getCppNameType() + ". Expected collection type");
        }


        TypedSymbol nested = SerializationUtils.clone(collection.findMethod("iterator", new ArrayList<>()).fullMatch());
        nested.setDisplayText(ctx.variable.getText());
        nested.setName(ctx.variable.getText());

        pushScope(new LocalScope());
        currentScope.addVariable(nested);
        codeWriter.write("for (" + nested.getCppNameType() + " " + nested.getDisplayText() +
                " : " + collection.getDisplayText() + ")");
        visit(ctx.suite());

        popScope();
        return null;
    }

    @Override
    public Symbol visitAdditiveExpression(TypePythonParser.AdditiveExpressionContext ctx) {
        TypedSymbol left = (TypedSymbol) visit(ctx.expr());
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()));
        TypedSymbol right = (TypedSymbol)visit(ctx.term());
        return CompoundTypedSymbol.of(detectAccurateType(left, right), left, mathOperator, right);
    }

    @Override
    public Symbol visitMultiplicativeExpression(TypePythonParser.MultiplicativeExpressionContext ctx) {
        TypedSymbol left = (TypedSymbol) visit(ctx.term());
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()));
        TypedSymbol right = (TypedSymbol)visit(ctx.factor());
        return CompoundTypedSymbol.of(detectAccurateType(left, right), left, mathOperator, right);
    }

    private Type detectAccurateType(TypedSymbol ... typedSymbols) {
        List<Type> types = Arrays.stream(typedSymbols).map(TypedSymbol::getVariableType).collect(Collectors.toList());
        return TypeAnalyzer.detectNestedType(types);
    }

    @Override
    public Symbol visitSignFactor(TypePythonParser.SignFactorContext ctx) {
        Optional<Symbol> sign = ofNullable(ctx.sign).map(Token::getText).map(MathOperator::translate).map(Symbol::new);
        VariableSymbol visit = (VariableSymbol) visit(ctx.factor());

        return sign.map(symbol -> (Symbol) CompoundTypedSymbol.of(visit.getVariableType(), symbol.getDisplayText() + visit.getDisplayText(), symbol, visit)).orElse(visit);
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
                List<VariableSymbol> symbols = ctx.arguments().argument()
                        .stream()
                        .map(this::visit)
                        .map(it -> (VariableSymbol) it)
                        .collect(Collectors.toList());

                String symbolText = symbols.stream().map(Symbol::getDisplayText).collect(Collectors.joining(","));
                List<Type> types = symbols.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList());
                Type variableType = TypeAnalyzer.detectNestedType(types);

                return TupleSymbolFactory.create("{" + symbolText + "}", variableType);
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public Symbol visitVariableDeclaration(TypePythonParser.VariableDeclarationContext ctx) {
        TypedSymbol symbol = (TypedSymbol) visit(ctx.type());
        symbol.setDisplayText(ctx.IDENTIFIER().getText());
        symbol.setName(ctx.IDENTIFIER().getText());
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
        if (ctx.arguments() == null) {
            ListSymbol listSymbol = ListSymbolFactory.create("", null);
            listSymbol.setDisplayText("{}");
            return listSymbol;
        }

        List<VariableSymbol> symbols = ctx.arguments().argument()
                .stream()
                .map(this::visit)
                .map(it -> (VariableSymbol) it)
                .collect(Collectors.toList());

        String symbolText = symbols.stream().map(Symbol::getDisplayText).collect(Collectors.joining(","));
        List<Type> types = symbols.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList());
        Type variableType = TypeAnalyzer.detectNestedType(types);

        return ListSymbolFactory.create("{" + symbolText + "}", variableType);
    }

    @Override
    public Symbol visitSetAtom(TypePythonParser.SetAtomContext ctx) {
        return super.visitSetAtom(ctx);
    }

    private void detectNested(List<Type> symbols, Type variableSymbol) {
        List<Type> nestedSymbols = symbols
                .stream()
                .map(it -> ((GenericType) it).getTemplateType(ListSymbol.GENERIC_TEMPLATE_NAME))
                .collect(Collectors.toList());
        GenericType nested = (GenericType) ((GenericType) variableSymbol).getTemplateType(ListSymbol.GENERIC_TEMPLATE_NAME);
        nested.setTemplateNameType(ListSymbol.GENERIC_TEMPLATE_NAME, TypeAnalyzer.detectNestedType(nestedSymbols));
        if (nested instanceof GenericType) {
            detectNested(nestedSymbols, nested);
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

        ListIterator<Symbol> trailerSymbolIterator = trailerSymbols.listIterator();
        while (trailerSymbolIterator.hasNext()) {
            Symbol trailerSymbol = trailerSymbolIterator.next();
            if (trailerSymbol.getSymbolType() == SymbolType.FUNCTION_CALL) {

                CompoundTypedSymbol compoundTypedSymbol = (CompoundTypedSymbol) trailerSymbol;
                MatchingResult resultFunctionMatching = currentScope.findFunction(atom.getName(), compoundTypedSymbol.getVariableTypes());
                if (resultFunctionMatching.getMatchType() == MatchType.NONE) {
                    throw new NoMatchingFunctionException(atom.getName());
                }
                FunctionSymbol function = resultFunctionMatching.getFunctionSymbol();
                resultSymbol = SerializationUtils.clone(function);
                FunctionResult invoke = function.invoke(resultSymbol, compoundTypedSymbol.getSymbols());
                resultSymbol.setDisplayText(invoke.getDisplayText());
                resultSymbol.setVariableType(invoke.getType());
                resultSymbol.setAssignable(invoke.isAssignable());
            }

            if (trailerSymbol.getSymbolType() == SymbolType.GET_COLLECTION_ELEMENT) {
                TypedSymbol index = (TypedSymbol) ((CompoundTypedSymbol) trailerSymbol).getSymbols().get(0);
                TypedSymbol element = SerializationUtils.clone(resultSymbol)
                        .findMethod("[]", Collections.singletonList(index.getVariableType()))
                        .minPartial();
                element.setDisplayText(resultSymbol.getDisplayText() + trailerSymbol.getDisplayText());
                element.setCollectionElement(true);
                resultSymbol = element;
            }

            if (trailerSymbol.getSymbolType() == SymbolType.FILED_IDENTIFIER && trailerSymbolIterator.hasNext()) {
                Symbol next = trailerSymbolIterator.next();
                if (next.getSymbolType() != SymbolType.FUNCTION_CALL) {
                    trailerSymbolIterator.previous();
                    break;
                }

                List<Symbol> parameters = ((CompoundTypedSymbol) next).getSymbols();
                FunctionSymbol element = SerializationUtils.clone(resultSymbol)
                        .findMethod(trailerSymbol.getName(), parameters.stream()
                                .map(it -> (TypedSymbol) it)
                                .map(TypedSymbol::getVariableType)
                                .collect(Collectors.toList()))
                        .minPartial();
                if (resultSymbol.getSymbolType() == SymbolType.IMPORT) {
                    element.setDisplayText(resultSymbol.getDisplayText() + element.getDisplayText() + next.getDisplayText());
                    popScope();
                } else {
                    FunctionResult functionResult = element.invoke(resultSymbol, parameters);
                    element.setAssignable(functionResult.isAssignable());
                    element.setDisplayText(resultSymbol.getDisplayText() + "." + functionResult.getDisplayText());
                    element.setVariableType(functionResult.getType());
                }
                resultSymbol = element;
            }
        }

        return resultSymbol;
    }

    @Override
    public Symbol visitTrailerIdentifier(TypePythonParser.TrailerIdentifierContext ctx) {
        return new Symbol(SymbolType.FILED_IDENTIFIER, ctx.IDENTIFIER().getText());
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
        return CompoundTypedSymbol.of(new ArrayList<>(), SymbolType.FUNCTION_CALL, "()");
    }

    @Override
    public Symbol visitTrailerBrackets(TypePythonParser.TrailerBracketsContext ctx) {
        TypedSymbol typedSymbol = (TypedSymbol) visit(ctx.argument());
        Symbol symbol = CompoundTypedSymbol.of(typedSymbol.getVariableType(), Collections.singletonList(typedSymbol));
        symbol.setSymbolType(SymbolType.GET_COLLECTION_ELEMENT);
        symbol.setDisplayText("[" + symbol.getDisplayText() + "]");
        return symbol;
    }

    private String getTrailerText(TypedSymbol symbol) {
        if (symbol instanceof CollectionClassSymbol && !symbol.isDeclaredInScope()) {
            return symbol.getCppNameType() + symbol.getDisplayText();
        }
        return symbol.getDisplayText();
    }

    @Override
    public Symbol visitGenericType(TypePythonParser.GenericTypeContext ctx) {
        String genericType = ctx.IDENTIFIER().getText();
        if (!SupportedGenericType.isSupported(genericType)) {
            throw new UnsupportedGenericTypeException(genericType);
        }

        TypedSymbol visit = (TypedSymbol) visit(ctx.type());

        Type variableType = SupportedGenericType.translate(genericType);
        if (variableType == LIST) {
            return ListSymbolFactory.create("", visit.getVariableType());
        }
        throw new UnsupportedGenericTypeException(visit.getDisplayText());
    }

    @Override
    public Symbol visitFileInput(TypePythonParser.FileInputContext ctx) {
        currentScope.addFunctionSymbol(new PrintFunction());
        currentScope.addFunctionSymbol(new LenFunction());
        currentScope.addFunctionSymbol(new FilterFunction());
        currentScope.addFunctionSymbol(new MapFunction());
        codeWriter.writeStartMain();
        ctx.children.forEach(this::visit);
        codeWriter.writeEndMain();
        return null;
    }

    @Override
    public Symbol visitImportStatement(TypePythonParser.ImportStatementContext ctx) {
        Symbol visit = visit(ctx.dottedIdentifier());
        String filePath = visit.getDisplayText();
        String namespace = filePath.replaceAll("/", "_");
        Scope compile = Compiler.compile("/" + filePath + ".tpy", new LibraryConsoleCodeWriter(filePath, namespace), new ImportScope(filePath, namespace));
        currentScope.addImportScope((ImportScope) compile);
        codeWriter.writeMain(namespace + "::" + namespace + "();");
        return null;
    }

    @Override
    public Symbol visitDottedIdentifier(TypePythonParser.DottedIdentifierContext ctx) {
        String path = ctx.IDENTIFIER().stream().map(ParseTree::getText).collect(Collectors.joining("/"));
        codeWriter.writeInclude("#include \"" + path + ".h\"");
        return new Symbol(path);
    }

    @Override
    public Symbol visitExecuteStatement(TypePythonParser.ExecuteStatementContext ctx) {
        Symbol atom = visit(ctx.atom());
        Symbol symbol = handleTrailerSymbols(ctx.trailer(), atom);
        codeWriter.write(symbol.getDisplayText() + ";");
        return null;
    }

    //TODO user reference vector<int> &test = ddd; ??
    @Override
    public Symbol visitAssignableExpressionStatement(TypePythonParser.AssignableExpressionStatementContext ctx) {
        TypedSymbol assignable = (TypedSymbol) visit(ctx.assignable());
        TypedSymbol symbol = (TypedSymbol) visit(ctx.test());

        if (!assignable.isAssignable()) {
            throw new ElementDoesNotSupportAssignmentException(assignable.getDisplayText());
        }

        if (symbol.getVariableType() == null) {
            throw new RuntimeException();
        }

        if (assignable.getVariableType() == null) {
            if (symbol instanceof ListSymbol) {
                if (((GenericType) symbol.getVariableType()).getTemplateType(ListSymbol.GENERIC_TEMPLATE_NAME) == null) {
                    throw new TypeNotDefinedException(assignable.getDisplayText());
                }
                String text = assignable.getDisplayText();
                assignable = new ListSymbol(symbol.getVariableType());
                assignable.setName(text);
                assignable.setDisplayText(text);
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
        TypedSymbol typedSymbol = currentScope.findAtom(ctx.getText()).orElseThrow(() -> new UndefinedVariableException(ctx.getText()));

        if (typedSymbol.getSymbolType() == SymbolType.IMPORT) {
            typedSymbol.getScope().ifPresent(this::pushScope);
        }

        return typedSymbol;
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

    public Scope getCurrentScope() {
        return currentScope;
    }
}
