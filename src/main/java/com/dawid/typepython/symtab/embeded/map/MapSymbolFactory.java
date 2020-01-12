package com.dawid.typepython.symtab.embeded.map;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.embeded.pair.PairSymbol;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.PropertySymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dawid on 02.11.2019 at 11:04.
 */
public class MapSymbolFactory {
    public static MapSymbol create(String name, Type keyType, Type valueType, TokenSymbolInfo tokenSymbolInfo) {
        MethodSymbol getSymbol = new MethodSymbol("[]", valueType, Collections.singletonList(new TypedSymbol(keyType, null)), null);
        MethodSymbol clear = new MethodSymbol("clear", CppVariableType.VOID, new ArrayList<>(), false, null);
        MethodSymbol pop = new MethodSymbol("delete", CppVariableType.VOID, Collections.singletonList(new TypedSymbol(keyType, null)), null);
        pop.setDisplayText("erase");

        GenericType pair = createPairGeneric(keyType, valueType);
        MethodSymbol iteratorSymbol = new MethodSymbol("iterator", pair, new ArrayList<>(), null);

        List<MethodSymbol> methods = new ArrayList<>();
        methods.add(getSymbol);
        methods.add(clear);
        methods.add(pop);
        methods.add(iteratorSymbol);
        GenericType variableType = new GenericType(SupportedGenericType.MAP, MapSymbol.KEY_TEMPLATE, keyType, methods);
        variableType.setTemplateNameType(MapSymbol.VALUE_TEMPLATE, valueType);
        MapSymbol mapSymbol = new MapSymbol(name, variableType, tokenSymbolInfo);
        mapSymbol.setTemporary(true);
        return mapSymbol;
    }

    private static GenericType createPairGeneric(Type keyType, Type valueType) {
        List<MethodSymbol> pairMethods = new ArrayList<>();
        MethodSymbol getKey = new PropertySymbol("key", keyType, new ArrayList<>());
        getKey.setDisplayText("first");
        MethodSymbol getValue = new PropertySymbol("value", valueType, new ArrayList<>());
        getValue.setDisplayText("second");
        pairMethods.add(getKey);
        pairMethods.add(getValue);
        GenericType pair = new GenericType(SupportedGenericType.PAIR, PairSymbol.FIRST_ELEMENT, keyType, pairMethods);
        pair.setTemplateNameType(PairSymbol.SECOND_ELEMENT, valueType);
        return pair;
    }
}
