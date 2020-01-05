package com.dawid.typepython.symtab.embeded.list;


import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dawid on 13.09.2019 at 13:37.
 */
public class ListSymbolFactory {
    public static StandardCollectionSymbol create(String name, Type templateType, TokenSymbolInfo tokenSymbolInfo) {

        MethodSymbol iteratorSymbol = new MethodSymbol("iterator", templateType, new ArrayList<>(), null);
        MethodSymbol getSymbol = new MethodSymbol("[]", templateType, Collections.singletonList(new TypedSymbol(CppVariableType.INT, null)),  null);
        MethodSymbol append = new MethodSymbol("append", CppVariableType.VOID, Collections.singletonList(new TypedSymbol(templateType, null)),  null);
        append.setDisplayText("push_back");
        MethodSymbol pop = new EraseMethodSymbol("delete", new FunctionType(CppVariableType.VOID),  Collections.singletonList(new TypedSymbol(CppVariableType.INT, null)));
        pop.setDisplayText("erase");
        MethodSymbol clear = new MethodSymbol("clear", CppVariableType.VOID, new ArrayList<>(),  null);

        List<MethodSymbol> methods = new ArrayList<>();
        methods.add(iteratorSymbol);
        methods.add(getSymbol);
        methods.add(append);
        methods.add(pop);
        methods.add(clear);

        GenericType variableType = createVariableType(templateType, methods);
        return new StandardCollectionSymbol(name, variableType, tokenSymbolInfo);
    }

    private static GenericType createVariableType(Type templateType, List<MethodSymbol> methods) {
        if (templateType != null) {
            return new GenericType(SupportedGenericType.LIST, StandardCollectionSymbol.GENERIC_TEMPLATE_NAME, templateType, methods);
        }
        return null;
    }

}
