package com.dawid.typepython.cpp.code;

import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.symbol.TypedSymbol;

public interface CodeWriter {
    void writeInclude(String include);
    void writeNamespace(String namespace);
    void writeGlobal(String code);
    void writeMain(String code);
    void finish();

    void setScope(Scope scope);

    void writeStartMain();

    void writeEndMain();

    void writeAssignment(TypedSymbol assignable, TypedSymbol symbol);

    void startScope();

    void endScope();

    void write(String code);

    void startFunction();

    void endFunction();

    void writeFunctionParameters(String functionParameters);

    void writeFunctionDeclaration(String functionReturnType, String functionIdentifier);

    void writeDeclaration(TypedSymbol assignable);

    void writeGlobalVariableDeclaration(TypedSymbol assignable, TypedSymbol symbol);
}
