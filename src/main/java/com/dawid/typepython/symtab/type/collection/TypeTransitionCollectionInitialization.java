package com.dawid.typepython.symtab.type.collection;

import com.dawid.typepython.symtab.type.VariableType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import type.CppVariableType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dawid on 21.07.2019 at 16:46.
 */
@EqualsAndHashCode
@AllArgsConstructor
class TypeTransitionCollectionInitialization {
    private VariableType currentType;
    private VariableType nextType;

    private static Map<TypeTransitionCollectionInitialization, VariableType> typeAccurateTransition = new HashMap<>();
    static {
        typeAccurateTransition.put(of(CppVariableType.BOOLEAN, CppVariableType.INT), CppVariableType.INT);
        typeAccurateTransition.put(of(CppVariableType.BOOLEAN, CppVariableType.LONG), CppVariableType.LONG);
        typeAccurateTransition.put(of(CppVariableType.BOOLEAN, CppVariableType.FLOAT), CppVariableType.FLOAT);
        typeAccurateTransition.put(of(CppVariableType.BOOLEAN, CppVariableType.DOUBLE), CppVariableType.DOUBLE);
        typeAccurateTransition.put(of(CppVariableType.INT, CppVariableType.LONG), CppVariableType.LONG);
        typeAccurateTransition.put(of(CppVariableType.INT, CppVariableType.FLOAT), CppVariableType.FLOAT);
        typeAccurateTransition.put(of(CppVariableType.INT, CppVariableType.DOUBLE), CppVariableType.DOUBLE);
        typeAccurateTransition.put(of(CppVariableType.LONG, CppVariableType.FLOAT), CppVariableType.DOUBLE);
        typeAccurateTransition.put(of(CppVariableType.LONG, CppVariableType.DOUBLE), CppVariableType.DOUBLE);
        typeAccurateTransition.put(of(CppVariableType.FLOAT, CppVariableType.DOUBLE), CppVariableType.DOUBLE);
    }

    public static TypeTransitionCollectionInitialization of(VariableType currentType, VariableType nextType) {
        return new TypeTransitionCollectionInitialization(currentType, nextType);
    }

    public VariableType getAccurateType() {
        return typeAccurateTransition.getOrDefault(this, currentType);
    }
}
