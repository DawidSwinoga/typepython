package com.dawid.typepython.symtab.embeded.list;


import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.Type;
import type.CppVariableType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dawid on 13.09.2019 at 13:37.
 */
public class ListSymbolFactory {
    public static ListSymbol create(String name, Type templateType) {

        MethodSymbol iteratorSymbol = new MethodSymbol("iterator", new FunctionType(templateType), new ArrayList<>(), false);
        MethodSymbol getSymbol = new MethodSymbol("[]", new FunctionType(templateType), Collections.singletonList(new TypedSymbol(CppVariableType.INT)), false);
        MethodSymbol append = new MethodSymbol("append", new FunctionType(CppVariableType.VOID), Collections.singletonList(new TypedSymbol(templateType)), false);
        append.setDisplayText("push_back");
        MethodSymbol pop = new EraseMethodSymbol("delete", new FunctionType(CppVariableType.VOID),  Collections.singletonList(new TypedSymbol(CppVariableType.INT)));
        pop.setDisplayText("erase");
        MethodSymbol clear = new MethodSymbol("clear", new FunctionType(CppVariableType.VOID), Collections.singletonList(new TypedSymbol(templateType)), false);

        List<MethodSymbol> methods = new ArrayList<>();
        methods.add(iteratorSymbol);
        methods.add(getSymbol);
        methods.add(append);
        methods.add(pop);
        methods.add(clear);
        GenericType variableType = new GenericType(SupportedGenericType.LIST, ListSymbol.GENERIC_TEMPLATE_NAME, templateType, methods);
        ListSymbol listSymbol = new ListSymbol(name, variableType);
        return listSymbol;
    }

}
