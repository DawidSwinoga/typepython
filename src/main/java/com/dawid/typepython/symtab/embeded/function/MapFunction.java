package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.FunctionResult;
import com.dawid.typepython.symtab.embeded.list.StandardCollectionSymbol;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.symbol.Symbol;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.FunctionType;
import com.dawid.typepython.symtab.type.GenericType;
import com.dawid.typepython.symtab.type.Type;

import java.util.List;

/**
 * Created by Dawid on 13.10.2019 at 17:51.
 */
public class MapFunction extends EmbeddedFunction{
    public MapFunction() {
        super("map");
        setDisplayText("stdtpy::map");
    }

    @Override
    public MatchType parametersMatch(List<Type> parameterTypes) {
        if (parameterTypes.size() < 2) {
            return MatchType.NONE;
        }

        if (!parameterTypes.get(0).isCollection() || !(parameterTypes.get(1) instanceof FunctionType)) {
            return MatchType.NONE;
        }

        FunctionType functionType = (FunctionType) parameterTypes.get(1);
        GenericType genericType = (GenericType) parameterTypes.get(0);
        Type embeddedType = genericType.getTemplateType(StandardCollectionSymbol.GENERIC_TEMPLATE_NAME);

        if (embeddedType.match(functionType.getParameters().get(0)) != MatchType.FULL) {
            return MatchType.NONE;
        }

        return MatchType.FULL;
    }

    @Override
    public FunctionResult invoke(Symbol invoker, List<TypedSymbol> parameters) {
        TypedSymbol collection = (TypedSymbol) parameters.get(0);
        Symbol function = parameters.get(1);

        String text = getDisplayText() + "(" + collection.getDisplayText() + ", " + function.getDisplayText() + ")";
        return new FunctionResult(text, collection.getVariableType());
    }

    @Override
    public int getParametersCount() {
        return 2;
    }
}
