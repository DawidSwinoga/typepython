package com.dawid.typepython.symtab.embeded.function;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.type.Type;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Dawid on 29.11.2019 at 21:31.
 */
public class PrintLineFunction extends PrintFunction {
    public PrintLineFunction() {
        super("println");
    }

    @Override
    protected StringBuilder print(List<TypedSymbol> parameters) {
        StringBuilder result = super.print(parameters);

        if (StringUtils.isEmpty(result)) {
            result.append("std::cout ");
        }
        return result.append(" << std::endl");
    }

    @Override
    public MatchType parametersMatch(List<Type> parameterTypes) {
        if (CollectionUtils.isEmpty(parameterTypes)) {
            return MatchType.FULL;
        }

        return super.parametersMatch(parameterTypes);
    }
}
