package com.dawid.typepython.symtab.type.collection;

import com.dawid.typepython.symtab.embeded.list.ListSymbol;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.Type;
import type.CppVariableType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 21.07.2019 at 01:09.
 */
public class TypeAnalyzer {


    public static Type detectNestedType(List<Type> symbols) {
        boolean collection = symbols.stream().allMatch(Type::isCollection);

        if (collection) {
            return detectCollectionType(symbols);
        }
        return findTheMostAccurateType(symbols);
    }

    private static Type findTheMostAccurateType(List<Type> symbols) {
        if (allSymbolsAreNumeric(symbols)) {
            return findTheMostAccurateNumericType(symbols);
        }

        if (allSymbolsAreString(symbols)) {
            return CppVariableType.STRING;
        }

        throw new CollectionTypesMissmatchException();
    }

    private static boolean allSymbolsAreString(List<Type> symbols) {
        return symbols.stream().allMatch(CppVariableType.STRING::equals);
    }

    private static Type findTheMostAccurateNumericType(List<Type> symbols) {
        Type currentType = symbols.stream().findFirst().orElse(CppVariableType.BOOLEAN);

        for (Type typedSymbol : symbols) {
            currentType = TypeTransitionCollectionInitialization.of(currentType, typedSymbol).getAccurateType();
        }

        return currentType;
    }

    private static boolean allSymbolsAreNumeric(List<Type> symbols) {
        return symbols.stream().allMatch(Type::isNumeric);
    }

    private static Type detectCollectionType(List<Type> symbols) {
        List<GenericType> types = symbols.stream().map(it -> (GenericType)it).collect(Collectors.toList());
        Optional<GenericType> first = types.stream().findFirst();
        return first.map(it -> detectCollectionType(it, types)).orElseThrow(CollectionTypesMissmatchException::new);
    }

    private static Type detectCollectionType(GenericType first, List<GenericType> symbols) {
        boolean collectionTypeMatch = symbols.stream().allMatch(it -> it.getGenericType().equals(first.getGenericType()));
        if (!collectionTypeMatch) {
            throw new CollectionTypesMissmatchException("Collection elements types not match to " + first.getPythonType());
        }

        Type templateType = detectNestedType(getTemplateTypes(symbols));
        first.setTemplateNameType(ListSymbol.GENERIC_TEMPLATE_NAME, templateType);

        return first;
    }

    private static List<Type> getTemplateTypes(List<GenericType> symbols) {
        return symbols.stream().map(it -> it.getTemplateType(ListSymbol.GENERIC_TEMPLATE_NAME)).collect(Collectors.toList());
    }
}
