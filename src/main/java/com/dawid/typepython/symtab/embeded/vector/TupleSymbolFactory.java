package com.dawid.typepython.symtab.embeded.vector;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.Type;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Created by Dawid on 15.10.2019 at 20:40.
 */
public class TupleSymbolFactory {
    public static TupleSymbol create(String name, Type templateType, TokenSymbolInfo tokenSymbolInfo) {

        MethodSymbol iteratorSymbol = new MethodSymbol("iterator", new FunctionType(templateType), new ArrayList<>(), false, null);
        MethodSymbol getSymbol = new MethodSymbol(
                "[]",
                templateType,
                singletonList(new TypedSymbol(CppVariableType.INT, null)),
                false, null
        );

        List<MethodSymbol> methods = new ArrayList<>();
        methods.add(iteratorSymbol);
        methods.add(getSymbol);
        GenericType variableType = new GenericType(SupportedGenericType.TUPLE, TupleSymbol.GENERIC_TEMPLATE_NAME, templateType, methods);
        return new TupleSymbol(name, variableType, tokenSymbolInfo);
    }
}
