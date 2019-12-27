package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.LibraryConsoleCodeWriter;
import com.dawid.typepython.cpp.code.generator.TemporaryVariableNameGenerator;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.embeded.function.FilterFunction;
import com.dawid.typepython.symtab.embeded.function.LenFunction;
import com.dawid.typepython.symtab.embeded.function.MapFunction;
import com.dawid.typepython.symtab.embeded.function.PrintFunction;
import com.dawid.typepython.symtab.embeded.function.PrintLineFunction;
import com.dawid.typepython.symtab.embeded.function.ToStringFunction;
import com.dawid.typepython.symtab.embeded.list.ListSymbolFactory;
import com.dawid.typepython.symtab.embeded.list.StandardCollectionSymbol;
import com.dawid.typepython.symtab.embeded.map.MapSymbol;
import com.dawid.typepython.symtab.embeded.map.MapSymbolFactory;
import com.dawid.typepython.symtab.embeded.set.SetSymbolFactory;
import com.dawid.typepython.symtab.embeded.vector.TupleSymbolFactory;
import com.dawid.typepython.symtab.literal.BooleanLiteral;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.matching.MatchingResult;
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
import com.dawid.typepython.symtab.symbol.ImportSymbol;
import com.dawid.typepython.symtab.symbol.KeyValueSymbol;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.UndefinedSymbolException;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import com.dawid.typepython.symtab.symbol.VariableTypeMissMatchException;
import com.dawid.typepython.symtab.type.CppVariableType;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dawid.typepython.PathUtils.getRelativePath;
import static com.dawid.typepython.symtab.type.SupportedGenericType.LIST;
import static com.dawid.typepython.symtab.type.SupportedGenericType.MAP;
import static com.dawid.typepython.symtab.type.SupportedGenericType.SET;
import static com.dawid.typepython.symtab.type.SupportedGenericType.TUPLE;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Symbol> {
    public static final String NAMESPACE_DELIMITER = "_";
    public static final String IDENTIFIER_DELIMITER = ".";
    public static final String TYPE_PYTHON_FILE_EXTENSION = ".tpy";
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
                .orElseGet(() -> new TypedSymbol("void", CppVariableType.VOID, new TokenSymbolInfo(ctx)));
        codeWriter.startFunction();
        codeWriter.writeFunctionDeclaration(returnType.getCppNameType(), ctx.IDENTIFIER().getText());
        visit(ctx.parameters());
        List<Type> parametersTypes = parameters.stream().map(TypedSymbol::getVariableType).collect(Collectors.toList());
        FunctionSymbol functionSymbol = new FunctionSymbol(ctx.IDENTIFIER().getText(), new FunctionType(returnType.getVariableType(), parametersTypes), parameters, new TokenSymbolInfo(ctx));
        functionSymbol.setScope(currentScope);
        validateFunctionUniqueness(functionSymbol, new TokenSymbolInfo(ctx));
        currentScope.addFunctionSymbol(functionSymbol);
        FunctionScope functionScope = new FunctionScope(ScopeType.LOCAL, parameters, returnType.getVariableType());
        pushScope(functionScope);

        validReturnStatement(ctx, functionScope);

        visit(ctx.suite());
        popScope();
        codeWriter.endFunction();
        return null;
    }

    private void validateFunctionUniqueness(FunctionSymbol functionSymbol, TokenSymbolInfo tokenSymbolInfo) {
        MatchingResult result = currentScope.findFunction(functionSymbol.getDisplayText(), functionSymbol.getParameterTypes(), tokenSymbolInfo);
        if (result.getMatchType() == MatchType.FULL) {
            throw new FunctionAlreadyExistException("Function: " + functionSymbol.getDisplayText() + " already exist", tokenSymbolInfo);
        }
    }

    private void validReturnStatement(TypePythonParser.FuncDefinitionContext ctx, FunctionScope functionScope) {

    }

    @Override
    public Symbol visitReturnStatement(TypePythonParser.ReturnStatementContext ctx) {
        Symbol returnSymbol = ofNullable(ctx.test()).map(this::visit).orElse(new Symbol("", new TokenSymbolInfo(ctx)));
        FunctionScope functionScope = currentScope.getFunctionScope();

        if (functionScope == null) {
            throw new CompilerException("Cannot define return outside of function.", new TokenSymbolInfo(ctx));
        }

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
                .map(this::createTypeParameterDeclaration)
                .collect(joining(","));
        codeWriter.writeFunctionParameters("(" + parametersString + ")");
        return null;
    }

    private String createTypeParameterDeclaration(TypedSymbol typedSymbol) {
        return typedSymbol.getCppNameType() + " " + (typedSymbol.isCollection() ? "&" : "") + typedSymbol.getDisplayText();
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
        TypedSymbol visit = (TypedSymbol) visit(ctx.test());
        String visitDisplayText = visit.getDisplayText();

        if (visit.isCollection()) {
            MatchingResult resultFunctionMatching = currentScope.findFunction("len", singletonList(visit.getVariableType()), visit.getTokenSymbolInfo());
            FunctionSymbol function = resultFunctionMatching.minPartial(visit.getTokenSymbolInfo());
            createTmpVariableForInlineInitilizerCollection(visit);
            visitDisplayText = function.invoke(visit, singletonList(visit)).getDisplayText();
        }
        codeWriter.write(visitDisplayText);
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
                    .orElseThrow(() -> new UndefinedSymbolException(collectionVariableName, variableSymbol.getTokenSymbolInfo()));
        } else {
            collection = variableSymbol;
            collection.setDisplayText(collection.getCppNameType() + collection.getDisplayText());
        }

        if (!collection.getVariableType().isCollection()) {
            throw new VariableTypeMissMatchException("Expected: collection type  | actual: " + collection.getTokenSymbolInfo().getSourceText() + " -> type: " + collection.getPythonNameType(), collection.getTokenSymbolInfo());
        }


        TypedSymbol nested = SerializationUtils.clone(collection.findMethod("iterator", new ArrayList<>(), collection.getTokenSymbolInfo()).fullMatch(collection.getTokenSymbolInfo()));
        nested.setDisplayText(ctx.variable.getText());
        nested.setName(ctx.variable.getText());

        pushScope(new LocalScope());
        currentScope.addVariable(nested);
        codeWriter.write("for (" + nested.getCppNameType() + " " + detectValueType(nested) + nested.getDisplayText() +
                " : " + collection.getDisplayText() + ")");
        visit(ctx.suite());

        popScope();
        return null;
    }

    private String detectValueType(TypedSymbol nested) {
        return nested.isCollection() && (nested.getVariableType().equals(SupportedGenericType.PAIR)) ? "&" : "";
    }

    @Override
    public Symbol visitAdditiveExpression(TypePythonParser.AdditiveExpressionContext ctx) {
        TypedSymbol left = (TypedSymbol) visit(ctx.expression());
        TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()), tokenSymbolInfo);
        TypedSymbol right = (TypedSymbol) visit(ctx.term());
        return CompoundTypedSymbol.of(tokenSymbolInfo, detectAdditiveAccurateType(tokenSymbolInfo, left, right), left, mathOperator, right);
    }

    private Type detectAdditiveAccurateType(TokenSymbolInfo tokenSymbolInfo, TypedSymbol left, TypedSymbol right) {
        if (left.getVariableType() == CppVariableType.STRING) {
            return CppVariableType.STRING;
        } else {
            return detectAccurateType(tokenSymbolInfo, left, right);
        }
    }

    @Override
    public Symbol visitMultiplicativeExpression(TypePythonParser.MultiplicativeExpressionContext ctx) {
        TypedSymbol left = (TypedSymbol) visit(ctx.term());
        TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
        Symbol mathOperator = new Symbol(MathOperator.translate(ctx.operator.getText()), tokenSymbolInfo);
        TypedSymbol right = (TypedSymbol) visit(ctx.factor());
        return CompoundTypedSymbol.of(tokenSymbolInfo, detectAccurateType(tokenSymbolInfo, left, right), left, mathOperator, right);
    }

    private Type detectAccurateType(TokenSymbolInfo tokenSymbolInfo, TypedSymbol... typedSymbols) {
        return TypeAnalyzer.detectNestedType(Arrays.asList(typedSymbols), tokenSymbolInfo);
    }

    @Override
    public Symbol visitSignFactor(TypePythonParser.SignFactorContext ctx) {
        Optional<Symbol> sign = ofNullable(ctx.sign).map(Token::getText).map(MathOperator::translate).map(it -> new Symbol(it, new TokenSymbolInfo(ctx)));
        VariableSymbol visit = (VariableSymbol) visit(ctx.factor());

        return sign.map(symbol -> (Symbol) CompoundTypedSymbol.of(new TokenSymbolInfo(ctx), visit.getVariableType(), symbol.getDisplayText() + visit.getDisplayText(), symbol, visit)).orElse(visit);
    }

    @Override
    public Symbol visitNegationTest(TypePythonParser.NegationTestContext ctx) {
        return CompoundTypedSymbol.of(new TokenSymbolInfo(ctx), CppVariableType.BOOLEAN, new Symbol(LogicalOperator.NOT.getCppOperator(), new TokenSymbolInfo(ctx)), visit(ctx.notTest()));
    }

    @Override
    public Symbol visitConditionalTupleAtom(TypePythonParser.ConditionalTupleAtomContext ctx) {
        if (ctx.arguments() != null) {
            CompoundTypedSymbol visit = (CompoundTypedSymbol) visit(ctx.arguments());

            TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
            if (visit.size() == 1) {
                return CompoundTypedSymbol.of(tokenSymbolInfo, visit.getVariableType(), new Symbol("(", null), visit, new Symbol(")", null));
            } else {
                List<VariableSymbol> symbols = ctx.arguments().argument()
                        .stream()
                        .map(this::visit)
                        .map(it -> (VariableSymbol) it)
                        .collect(Collectors.toList());

                String symbolText = symbols.stream().map(Symbol::getDisplayText).collect(joining(","));
                Type variableType = TypeAnalyzer.detectNestedType(symbols, tokenSymbolInfo);

                return TupleSymbolFactory.create("{" + symbolText + "}", variableType, tokenSymbolInfo);
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
        return new TypedSymbol(ctx.getText(), CppVariableType.translate(ctx.getText()), new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitArguments(TypePythonParser.ArgumentsContext ctx) {
        VariableSymbol first = (VariableSymbol) visit(ctx.first);
        if (ctx.argument().size() == 1) {
            return CompoundTypedSymbol.of(new TokenSymbolInfo(ctx), first.getVariableType(), first);
        }

        List<Symbol> symbols = ctx.argument().stream().map(this::visit).collect(Collectors.toList());
        return CompoundTypedSymbol.of(first.getVariableType(), first, symbols, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitListAtom(TypePythonParser.ListAtomContext ctx) {
        TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
        if (ctx.arguments() == null) {
            StandardCollectionSymbol standardCollectionSymbol = ListSymbolFactory.create("", null, tokenSymbolInfo);
            standardCollectionSymbol.setDisplayText("{}");
            return standardCollectionSymbol;
        }

        List<VariableSymbol> symbols = ctx.arguments().argument()
                .stream()
                .map(this::visit)
                .map(it -> (VariableSymbol) it)
                .collect(Collectors.toList());

        String symbolText = symbols.stream().map(Symbol::getDisplayText).collect(joining(","));
        Type variableType = TypeAnalyzer.detectNestedType(symbols, tokenSymbolInfo);

        return ListSymbolFactory.create("{" + symbolText + "}", variableType, tokenSymbolInfo);
    }

    @Override
    public Symbol visitSetAtom(TypePythonParser.SetAtomContext ctx) {
        TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
        if (ctx.arguments() == null) {
            StandardCollectionSymbol standardCollectionSymbol = SetSymbolFactory.create("", null, tokenSymbolInfo);
            standardCollectionSymbol.setDisplayText("");
            return standardCollectionSymbol;
        }

        List<VariableSymbol> symbols = ctx.arguments().argument()
                .stream()
                .map(this::visit)
                .map(it -> (VariableSymbol) it)
                .collect(Collectors.toList());

        Type variableType = TypeAnalyzer.detectNestedType(symbols, tokenSymbolInfo);
        String symbolText = symbols.stream().map(Symbol::getDisplayText).collect(joining(","));
        return SetSymbolFactory.create("{" + symbolText + "}", variableType, tokenSymbolInfo);
    }

    @Override
    public Symbol visitDictorySetMakersAtom(TypePythonParser.DictorySetMakersAtomContext ctx) {
        TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
        if (ctx.dictionarySetMakers() == null) {
            StandardCollectionSymbol standardCollectionSymbol = ListSymbolFactory.create("", null, tokenSymbolInfo);
            standardCollectionSymbol.setDisplayText("{}");
            return standardCollectionSymbol;
        }

        List<KeyValueSymbol> symbols = ctx.dictionarySetMakers().dictionarySetMaker()
                .stream()
                .map(this::visit)
                .map(it -> (KeyValueSymbol) it)
                .collect(Collectors.toList());

        String symbolText = symbols.stream().map(Symbol::getDisplayText).collect(joining(","));


        Type keyType = detectType(symbols.stream().map(KeyValueSymbol::getKey).collect(Collectors.toList()), tokenSymbolInfo);
        Type valueType = detectType(symbols.stream().map(KeyValueSymbol::getKey).collect(Collectors.toList()), tokenSymbolInfo);

        return MapSymbolFactory.create("{" + symbolText + "}", keyType, valueType, tokenSymbolInfo);
    }

    private Type detectType(List<TypedSymbol> symbols, TokenSymbolInfo tokenSymbolInfo) {
        return TypeAnalyzer.detectNestedType(symbols, tokenSymbolInfo);
    }

    @Override
    public Symbol visitDictionarySetMaker(TypePythonParser.DictionarySetMakerContext ctx) {
        return new KeyValueSymbol((TypedSymbol) visit(ctx.key), (TypedSymbol) visit(ctx.value));
    }

    @Override
    public Symbol visitConditionalPower(TypePythonParser.ConditionalPowerContext ctx) {
        Symbol atom = visit(ctx.atomExpression());

        if (ctx.exponent != null) {
            Symbol factor = visit(ctx.factor());
            Symbol symbol = new Symbol("std::pow(", null);
            return CompoundTypedSymbol.of(new TokenSymbolInfo(ctx), CppVariableType.DOUBLE, symbol, atom,
                    new Symbol(",", null), factor, new Symbol(")", null));
        }

        return atom;
    }

    @Override
    public Symbol visitAtomExpression(TypePythonParser.AtomExpressionContext ctx) {
        List<TypePythonParser.TrailerContext> trailers = ctx.trailer();
        Symbol symbol = prepareAtom(ctx.atom(), trailers);

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
        List<TypePythonParser.TrailerContext> trailers = ctx.trailer();
        Symbol symbol = prepareAtom(ctx.atom(), trailers);

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
                MatchingResult resultFunctionMatching = currentScope.findFunction(atom.getName(), compoundTypedSymbol.getVariableTypes(), atom.getTokenSymbolInfo());
                FunctionSymbol function = resultFunctionMatching.minPartial(atom.getTokenSymbolInfo());
                resultSymbol = SerializationUtils.clone(function);
                FunctionResult invoke = function.invoke(resultSymbol, createTmpVariableForInlineInitilizerCollection(compoundTypedSymbol));
                resultSymbol.setDisplayText(invoke.getDisplayText());
                resultSymbol.setVariableType(invoke.getType());
                resultSymbol.setAssignable(invoke.isAssignable());
            }

            if (trailerSymbol.getSymbolType() == SymbolType.GET_COLLECTION_ELEMENT) {
                TypedSymbol index = (TypedSymbol) createTmpVariableForInlineInitilizerCollection((CompoundTypedSymbol) trailerSymbol).get(0);
                TypedSymbol element = SerializationUtils.clone(resultSymbol)
                        .findMethod("[]", singletonList(index.getVariableType()), trailerSymbol.getTokenSymbolInfo())
                        .minPartial(trailerSymbol.getTokenSymbolInfo());
                element.setDisplayText(resultSymbol.getDisplayText() + trailerSymbol.getDisplayText());
                element.setCollectionElement(true);
                element.setAssignable((((MethodSymbol) element).isReturnTypeAssignable()));
                resultSymbol = element;
            }

            if (trailerSymbol.getSymbolType() == SymbolType.FILED_IDENTIFIER) {
                TypedSymbol element;


                if (trailerSymbolIterator.hasNext() && trailerSymbolIterator.next().getSymbolType() == SymbolType.FUNCTION_CALL) {
                    trailerSymbolIterator.previous();
                    Symbol next = trailerSymbolIterator.next();

                    List<TypedSymbol> parameters = createTmpVariableForInlineInitilizerCollection((CompoundTypedSymbol) next);
                    element = SerializationUtils.clone(resultSymbol)
                            .findMethod(trailerSymbol.getName(), parameters.stream()
                                    .map(it -> (TypedSymbol) it)
                                    .map(TypedSymbol::getVariableType)
                                    .collect(Collectors.toList()), trailerSymbol.getTokenSymbolInfo())
                            .minPartial(trailerSymbol.getTokenSymbolInfo());

                    if (resultSymbol.getSymbolType() == SymbolType.IMPORT) {
                        element.setDisplayText(resultSymbol.getDisplayText() + element.getDisplayText() + next.getDisplayText());
                    } else {
                        FunctionResult functionResult = ((FunctionSymbol) element).invoke(resultSymbol, new ArrayList<>(parameters));
                        element.setAssignable(functionResult.isAssignable());
                        element.setDisplayText(resultSymbol.getDisplayText() + "." + functionResult.getDisplayText());
                        element.setVariableType(functionResult.getType());
                    }

                } else {
                    element = SerializationUtils.clone((ImportSymbol) resultSymbol)
                            .findVariable(trailerSymbol.getName())
                            .orElseThrow(() -> new UndefinedSymbolException(trailerSymbol.getName(), trailerSymbol.getTokenSymbolInfo()));
                    element.setDisplayText(resultSymbol.getDisplayText() + element.getDisplayText());
                }

                resultSymbol = element;
            }
        }

        return resultSymbol;
    }

    private List<TypedSymbol> createTmpVariableForInlineInitilizerCollection(CompoundTypedSymbol compoundTypedSymbol) {
        compoundTypedSymbol.getSymbols()
                .stream()
                .filter(it -> it instanceof TypedSymbol)
                .map(it -> ((TypedSymbol) it))
                .filter(TypedSymbol::isCollection)
                .filter(it -> !it.isDeclaredInScope())
                .forEach(this::createTmpVariableForInlineInitilizerCollection);
        return compoundTypedSymbol.getSymbols().stream().map(it -> (TypedSymbol) it).collect(Collectors.toList());
    }

    private void createTmpVariableForInlineInitilizerCollection(TypedSymbol typedSymbol) {
        String name = TemporaryVariableNameGenerator.INSTANCE.generateVariableName();
        TypedSymbol clone = SerializationUtils.clone(typedSymbol);
        typedSymbol.setDisplayText(name);
        typedSymbol.setName(name);
        typedSymbol.setTemporary(true);
        codeWriter.writeAssignment(typedSymbol, clone);
        currentScope.addVariable(typedSymbol);

    }

    @Override
    public Symbol visitTrailerIdentifier(TypePythonParser.TrailerIdentifierContext ctx) {
        return new Symbol(SymbolType.FILED_IDENTIFIER, ctx.IDENTIFIER().getText(), new TokenSymbolInfo(ctx));
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
                    .collect(joining(","));
            return CompoundTypedSymbol.of(arguments, SymbolType.FUNCTION_CALL, "(" + text + ")", new TokenSymbolInfo(ctx));
        }
        return CompoundTypedSymbol.of(new ArrayList<>(), SymbolType.FUNCTION_CALL, "()", new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitTrailerBrackets(TypePythonParser.TrailerBracketsContext ctx) {
        TypedSymbol typedSymbol = (TypedSymbol) visit(ctx.argument());
        Symbol symbol = CompoundTypedSymbol.of(typedSymbol.getVariableType(), singletonList(typedSymbol), new TokenSymbolInfo(ctx));
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
        TokenSymbolInfo tokenSymbolInfo = new TokenSymbolInfo(ctx);
        if (!SupportedGenericType.isSupported(genericType)) {
            throw new UnsupportedGenericTypeException(genericType, tokenSymbolInfo);
        }

        List<TypedSymbol> types = ctx.type().stream().map(this::visit).map(it -> (TypedSymbol) it).collect(Collectors.toList());

        Type variableType = SupportedGenericType.translate(genericType, tokenSymbolInfo);

        if (types.size() == 1) {
            Type type = types.get(0).getVariableType();
            if (variableType == LIST) {
                return ListSymbolFactory.create("", type, tokenSymbolInfo);
            }
            if (variableType == SET) {
                return SetSymbolFactory.create("", type, tokenSymbolInfo);
            }
            if (variableType == TUPLE) {
                return TupleSymbolFactory.create("", type, tokenSymbolInfo);
            }
        }

        if (types.size() == 2) {
            if (variableType == MAP) {
                TypedSymbol key = types.get(0);
                TypedSymbol value = types.get(1);

                return MapSymbolFactory.create("", key.getVariableType(), value.getVariableType(), tokenSymbolInfo);
            }
        }
        throw new UnsupportedGenericTypeException(genericType, tokenSymbolInfo);
    }

    @Override
    public Symbol visitFileInput(TypePythonParser.FileInputContext ctx) {
        currentScope.addFunctionSymbol(new PrintFunction());
        currentScope.addFunctionSymbol(new PrintLineFunction());
        currentScope.addFunctionSymbol(new LenFunction());
        currentScope.addFunctionSymbol(new FilterFunction());
        currentScope.addFunctionSymbol(new MapFunction());
        currentScope.addFunctionSymbol(new ToStringFunction());
        codeWriter.writeStartMain();
        ctx.children.forEach(this::visit);
        codeWriter.writeEndMain();
        return null;
    }

    @Override
    public Symbol visitImportStatement(TypePythonParser.ImportStatementContext ctx) {
        Symbol visit = visit(ctx.dottedIdentifier());
        String filePath = visit.getDisplayText();
        String namespace = filePath.replaceAll(Main.JAVA_CLASSPATH_SEPARATOR, NAMESPACE_DELIMITER);
        String scopeIdentifier = filePath.replaceAll(Main.JAVA_CLASSPATH_SEPARATOR, IDENTIFIER_DELIMITER);
        Scope compile = Compiler.compile(filePath + TYPE_PYTHON_FILE_EXTENSION, new LibraryConsoleCodeWriter(filePath, namespace), new ImportScope(filePath, namespace, scopeIdentifier), new TokenSymbolInfo(ctx));
        currentScope.addImportScope((ImportScope) compile);
        codeWriter.writeMain(namespace + "::" + namespace + "();");
        return null;
    }

    @Override
    public Symbol visitDottedIdentifier(TypePythonParser.DottedIdentifierContext ctx) {
        String path =ctx.IDENTIFIER().stream().map(ParseTree::getText).collect(joining(Main.JAVA_CLASSPATH_SEPARATOR));
        String pathRelative = getRelativePath(path, currentScope.getScopeFileName());
        codeWriter.writeInclude("#include \"" + pathRelative + ".h\"");
        return new Symbol(path, new TokenSymbolInfo(ctx));
    }



    @Override
    public Symbol visitExecuteStatement(TypePythonParser.ExecuteStatementContext ctx) {
        List<TypePythonParser.TrailerContext> trailer = ctx.trailer();
        Symbol atom = prepareAtom(ctx.atom(), trailer);
        Symbol symbol = handleTrailerSymbols(trailer, atom);
        codeWriter.write(symbol.getDisplayText() + ";");
        return null;
    }

    private Symbol prepareAtom(TypePythonParser.AtomContext atomContext, List<TypePythonParser.TrailerContext> trailers) {
        Optional<Symbol> atom = empty();
        try {
            atom = ofNullable(visit(atomContext));
        } catch (UndefinedSymbolException e) {
            String text = atomContext.getText();
            int possibleImportTrailerCount = trailers.size() - 1;
            for (int i = 0; i < possibleImportTrailerCount; i++) {
                text = text + trailers.get(i).getText();
                Optional<ImportScope> importScope = currentScope.findImport(text);
                if (importScope.isPresent()) {
                    trailers.removeAll(trailers.subList(0, i + 1));
                    atom = importScope.map(it -> new ImportSymbol(SymbolType.IMPORT, it));
                    break;
                }

            }
            if (!atom.isPresent()) {
                throw e;
            }
        }

        return atom.orElseThrow(() -> new UndefinedSymbolException(atomContext.getText(), new TokenSymbolInfo(atomContext)));
    }

    @Override
    public Symbol visitAssignableExpressionStatement(TypePythonParser.AssignableExpressionStatementContext ctx) {
        TypedSymbol assignable = (TypedSymbol) visit(ctx.assignable());
        TypedSymbol symbol = (TypedSymbol) visit(ctx.test());

        if (!assignable.isAssignable()) {
            throw new ElementDoesNotSupportAssignmentException(assignable);
        }

        if (symbol.getVariableType() == null) {
            throw new RuntimeException();
        }

        if (assignable.getVariableType() == null) {
            if (symbol instanceof StandardCollectionSymbol) {
                if (((GenericType) symbol.getVariableType()).getTemplateType(StandardCollectionSymbol.GENERIC_TEMPLATE_NAME) == null) {
                    throw new TypeNotDefinedException(assignable.getDisplayText(), assignable.getTokenSymbolInfo());
                }
                String text = assignable.getDisplayText();
                assignable = new StandardCollectionSymbol(symbol.getVariableType(), symbol.getTokenSymbolInfo());
                assignable.setName(text);
                assignable.setDisplayText(text);
            } else if (symbol instanceof MapSymbol) {
                GenericType genericType = (GenericType) symbol.getVariableType();
                if (genericType.getTemplateType(MapSymbol.KEY_TEMPLATE) == null || genericType.getTemplateType(MapSymbol.VALUE_TEMPLATE) == null) {
                    throw new TypeNotDefinedException(assignable.getDisplayText(), assignable.getTokenSymbolInfo());
                }
                String text = assignable.getDisplayText();
                assignable = new StandardCollectionSymbol(symbol.getVariableType(), symbol.getTokenSymbolInfo());
                assignable.setName(text);
                assignable.setDisplayText(text);
            } else {
                assignable.setVariableType(symbol.getVariableType());
            }
        } else {
            MatchType match = symbol.match(assignable);
            if (match == MatchType.NONE) {
                throw new VariableTypeMissMatchException(symbol.getTokenSymbolInfo(), assignable.getVariableType(), symbol.getVariableType());
            }
        }

        codeWriter.writeAssignment(assignable, symbol);
        if (!assignable.isDeclaredInScope()) {
            currentScope.addVariable(assignable);
        }

        return null;
    }

    @Override
    public Symbol visitVariableDeclarationStatement(TypePythonParser.VariableDeclarationStatementContext ctx) {
        TypedSymbol visit = (TypedSymbol) visit(ctx.variableDeclaration());
        codeWriter.writeDeclaration(visit);
        currentScope.addVariable(visit);
        return null;
    }

    @Override
    public Symbol visitIdentifierAtom(TypePythonParser.IdentifierAtomContext ctx) {
        TypedSymbol typedSymbol = currentScope.findAtom(ctx.getText()).orElseThrow(() -> new UndefinedSymbolException(ctx.getText(), new TokenSymbolInfo(ctx)));
        typedSymbol.setTokenSymbolInfo(new TokenSymbolInfo(ctx));

        return typedSymbol;
    }

    @Override
    public Symbol visitIntegerLiteral(TypePythonParser.IntegerLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.INT, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitStringLiteral(TypePythonParser.StringLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.STRING, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitOrStatement(TypePythonParser.OrStatementContext ctx) {
        Symbol left = visit(ctx.left);
        Symbol operator = new Symbol(LogicalOperator.translate(ctx.operator.getText()), new TokenSymbolInfo(ctx));
        Symbol right = visit(ctx.right);
        return CompoundTypedSymbol.of(new TokenSymbolInfo(ctx), CppVariableType.BOOLEAN, left, operator, right);
    }

    @Override
    public Symbol visitAndStatement(TypePythonParser.AndStatementContext ctx) {
        Symbol left = visit(ctx.left);
        Symbol operator = new Symbol(LogicalOperator.translate(ctx.operator.getText()), new TokenSymbolInfo(ctx));
        Symbol right = visit(ctx.right);
        return CompoundTypedSymbol.of(new TokenSymbolInfo(ctx), CppVariableType.BOOLEAN, left, operator, right);
    }

    @Override
    public Symbol visitComparison(TypePythonParser.ComparisonContext ctx) {
        if (!ctx.compareOperator().isEmpty()) {
            List<Symbol> symbols = ctx.children.stream().map(this::visit).collect(Collectors.toList());
            return new CompoundTypedSymbol(symbols, CppVariableType.BOOLEAN, new TokenSymbolInfo(ctx));
        }
        return super.visitComparison(ctx);
    }


    @Override
    public Symbol visitCompareOperator(TypePythonParser.CompareOperatorContext ctx) {
        return new Symbol(CompareOperator.translate(ctx.getText()), new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitLongLiteral(TypePythonParser.LongLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.LONG, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitDoubleLiteral(TypePythonParser.DoubleLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.DOUBLE, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitFloatLiteral(TypePythonParser.FloatLiteralContext ctx) {
        return new VariableSymbol(ctx.getText(), CppVariableType.FLOAT, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitBooleanLiteral(TypePythonParser.BooleanLiteralContext ctx) {
        return new VariableSymbol(BooleanLiteral.translate(ctx.getText()), CppVariableType.BOOLEAN, new TokenSymbolInfo(ctx));
    }

    @Override
    public Symbol visitAssignableIdentifier(TypePythonParser.AssignableIdentifierContext ctx) {
        Optional<TypedSymbol> variable = currentScope.findAtom(ctx.getText());
        return variable.orElseGet(() -> new VariableSymbol(ctx.getText(), new TokenSymbolInfo(ctx)));
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
