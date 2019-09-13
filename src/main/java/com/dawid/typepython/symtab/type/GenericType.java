package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.matching.MatchType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Dawid on 13.09.2019 at 16:55.
 */

@EqualsAndHashCode
public class GenericType implements Type {
    @Getter
    private final Type genericType;
    private final Map<String, Type> templateNameType = new LinkedHashMap<>();

    public GenericType(Type genericType) {
        this.genericType = genericType;
    }

    public GenericType(Type genericType, String templateName, Type templateType) {
        this.genericType = genericType;
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
                    .entrySet()
                    .stream()
                    .map(it -> it.getValue().getCppNameType())
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
        return genericType.getPythonType();
    }

    @Override
    public MatchType match(Type t) {
        if (equals(t)) {
            return MatchType.FULL;
        }

        return MatchType.NONE;
    }
}
