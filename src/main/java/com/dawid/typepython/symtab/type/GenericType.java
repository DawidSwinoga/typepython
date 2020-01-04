package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.symbol.MethodSymbol;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 13.09.2019 at 16:55.
 */

@EqualsAndHashCode(exclude = "methodSymbols")
public class GenericType implements Type {
    @Getter
    private final Type genericType;
    private final Map<String, Type> templateNameType = new LinkedHashMap<>();
    private final List<MethodSymbol> methodSymbols;


    public GenericType(Type genericType, String templateName, Type templateType, List<MethodSymbol> methodSymbols) {
        this.genericType = genericType;
        this.methodSymbols = methodSymbols;
        templateNameType.put(templateName, templateType);

    }

    public Type getTemplateType(String name) {
        return templateNameType.get(name);
    }

    public void setTemplateNameType(String name, Type type) {
        if (templateNameType.containsKey(name)) {
            templateNameType.replace(name, type);
        }

        templateNameType.put(name, type);
    }

    @Override
    public String getCppNameType() {
        StringBuilder type = new StringBuilder();
        type.append(genericType.getCppNameType());

        if (!templateNameType.isEmpty()) {
            type.append("<");
            String nestedType = templateNameType
                    .values()
                    .stream()
                    .map(Type::getCppNameType)
                    .collect(Collectors.joining(","));
            type.append(nestedType);
            type.append(">");
        }

        return type.toString();
    }

    @Override
    public boolean isCollection() {
        return genericType.isCollection();
    }

    @Override
    public boolean isGenericType() {
        return true;
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public String getPythonType() {
        StringBuilder type = new StringBuilder();
        type.append(genericType.getPythonType());

        if (!templateNameType.isEmpty()) {
            type.append("<");
            String nestedType = templateNameType
                    .values()
                    .stream()
                    .map(Type::getPythonType)
                    .collect(Collectors.joining(","));
            type.append(nestedType);
            type.append(">");
        }

        return type.toString();
    }

    @Override
    public List<MethodSymbol> getMethodSymbol() {
        return methodSymbols;
    }

    @Override
    public MatchType match(Type t) {
        if (equals(t)) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }
}
