package com.dawid.typepython.cpp.code.collection;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.symbol.VariableSymbol;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import type.CppVariableType;

import java.util.List;
import java.util.Optional;

/**
 * Created by Dawid on 21.07.2019 at 01:09.
 */
public class CollectionTypeAnalyzer {


    public static VariableType detectNestedType(List<TypedSymbol> symbols) {
        boolean collection = symbols.stream().map(TypedSymbol::getVariableType).anyMatch(VariableType::isCollection);

        if (collection) {
            return detectCollectionType(symbols);
        }
        return findTheMostAccurateType(symbols);
    }

    private static VariableType findTheMostAccurateType(List<TypedSymbol> symbols) {
        if (allSymbolsAreNumeric(symbols)) {
            return findTheMostAccurateNumericType(symbols);
        }

        if (allSymbolsAreString(symbols)) {
            return CppVariableType.STRING;
        }

        throw new CollectionTypesMissmatchException();
    }

    private static boolean allSymbolsAreString(List<TypedSymbol> symbols) {
        return symbols.stream().map(TypedSymbol::getVariableType).allMatch(CppVariableType.STRING::equals);
    }

    private static VariableType findTheMostAccurateNumericType(List<TypedSymbol> symbols) {
        VariableType currentType = symbols.stream().findFirst().map(TypedSymbol::getVariableType).orElse(CppVariableType.BOOLEAN);

        for (TypedSymbol typedSymbol : symbols) {
            currentType = TypeTransitionCollectionInitialization.of(currentType, typedSymbol.getVariableType()).getAccurateType();
        }

        return currentType;
    }

    private static boolean allSymbolsAreNumeric(List<TypedSymbol> symbols) {
        return symbols.stream().map(TypedSymbol::getVariableType).allMatch(VariableType::isNumeric);
    }

    private static VariableType detectCollectionType(List<TypedSymbol> symbols) {
        Optional<TypedSymbol> first = symbols.stream().findFirst();
        return first.map(it -> detectCollectionType(it, symbols)).orElseThrow(CollectionTypesMissmatchException::new);
    }

    private static VariableType detectCollectionType(TypedSymbol first, List<TypedSymbol> symbols) {
        boolean collectionTypeMatch = symbols.stream().map(TypedSymbol::getVariableType).allMatch(it -> it.equals(first.getVariableType()));
        if (!collectionTypeMatch) {
            throw new CollectionTypesMissmatchException("Collection types not parametersMatch to " + first.getVariableType().getPythonType());
        }

        return first.getVariableType();
    }
}
