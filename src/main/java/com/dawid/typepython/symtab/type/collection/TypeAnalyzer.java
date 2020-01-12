package com.dawid.typepython.symtab.type.collection;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.embeded.list.StandardCollectionSymbol;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.Type;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by Dawid on 21.07.2019 at 01:09.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeAnalyzer {
    private final List<Type> types;

    public static Type detectNestedType(List<? extends TypedSymbol> symbols, TokenSymbolInfo tokenSymbolInfo) {
        List<Type> types = symbols.stream().map(TypedSymbol::getVariableType).collect(toList());
        try {
            return new TypeAnalyzer(types).detectNestedType();
        } catch (TypeAnalyzerException e) {
            StringBuilder message = new StringBuilder(e.getMessage());
            message.append(" ")
                    .append(symbols.stream().map(Symbol::getDisplayText).collect(Collectors.joining(",")))
                    .append("  ->  ")
                    .append(types.stream().map(Type::getPythonType).collect(Collectors.joining(",")));
            throw new TypesMissMatchException(message.toString(), tokenSymbolInfo);
        }
    }

    private Type detectNestedType() {
        boolean collection = types.stream().allMatch(Type::isCollection);

        if (collection) {
            return detectCollectionType();
        }
        return findTheMostAccurateType();
    }

    private Type findTheMostAccurateType() {
        if (allSymbolsAreNumeric()) {
            return findTheMostAccurateNumericType();
        }

        if (allSymbolsAreString()) {
            return CppVariableType.STRING;
        }

        throw new TypeAnalyzerException("Expression cannot contains mixed types.");
    }

    private boolean allSymbolsAreString() {
        return types.stream().allMatch(CppVariableType.STRING::equals);
    }

    private Type findTheMostAccurateNumericType() {
        Type currentType = types.stream().findFirst().orElse(CppVariableType.BOOLEAN);

        for (Type typedSymbol : types) {
            currentType = TypeTransitionCollectionInitialization.of(currentType, typedSymbol).getAccurateType();
        }

        return currentType;
    }

    private boolean allSymbolsAreNumeric() {
        return types.stream().allMatch(Type::isNumeric);
    }

    private Type detectCollectionType() {
        List<GenericType> types = this.types.stream().map(it -> (GenericType) it).collect(toList());
        Optional<GenericType> first = types.stream().findFirst();
        return first.map(it -> detectCollectionType(it, types)).orElseThrow(TypeAnalyzerException::new);
    }

    private Type detectCollectionType(GenericType first, List<GenericType> genericTypes) {
        boolean collectionTypeMatch = genericTypes.stream().allMatch(it -> it.getGenericType().equals(first.getGenericType()));
        if (!collectionTypeMatch) {
            throw new TypeAnalyzerException("Collection elements types not match to type: " + first.getPythonType() + ".");
        }

        Type templateType = new TypeAnalyzer(getTemplateTypes(genericTypes)).detectNestedType();
        first.setTemplateNameType(StandardCollectionSymbol.GENERIC_TEMPLATE_NAME, templateType);

        return first;
    }

    private List<Type> getTemplateTypes(List<GenericType> genericTypes) {
        return genericTypes.stream().map(it -> it.getTemplateType(StandardCollectionSymbol.GENERIC_TEMPLATE_NAME)).collect(toList());
    }
}
