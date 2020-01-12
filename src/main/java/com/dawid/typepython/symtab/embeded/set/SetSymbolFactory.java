package com.dawid.typepython.symtab.embeded.set;

import com.dawid.typepython.TokenSymbolInfo;
import com.dawid.typepython.symtab.embeded.list.StandardCollectionSymbol;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.CppVariableType;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.SupportedGenericType;
import com.dawid.typepython.symtab.type.Type;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Dawid on 27.10.2019 at 18:59.
 */
public class SetSymbolFactory {
    public static StandardCollectionSymbol create(String name, Type templateType, TokenSymbolInfo tokenSymbolInfo) {

        ArrayList<MethodSymbol> methodSymbols = new ArrayList<>();
        MethodSymbol iteratorSymbol = new MethodSymbol("iterator", new FunctionType(templateType), new ArrayList<>(),  null);
        MethodSymbol append = new MethodSymbol("add", new FunctionType(CppVariableType.VOID), Collections.singletonList(new TypedSymbol(templateType, null)), false, null);
        append.setDisplayText("insert");
        MethodSymbol clear = new MethodSymbol("clear", new FunctionType(CppVariableType.VOID), new ArrayList<>(), false, null);

        methodSymbols.add(iteratorSymbol);
        methodSymbols.add(append);
        methodSymbols.add(clear);
        GenericType variableType = new GenericType(SupportedGenericType.SET, StandardCollectionSymbol.GENERIC_TEMPLATE_NAME, templateType, methodSymbols);
        String displayText = "std::set<" + templateType.getCppNameType() + ">(" + name + ")";
        StandardCollectionSymbol standardCollectionSymbol = new StandardCollectionSymbol(displayText, variableType, tokenSymbolInfo);
        standardCollectionSymbol.setTemporary(true);
        return standardCollectionSymbol;
    }
}
