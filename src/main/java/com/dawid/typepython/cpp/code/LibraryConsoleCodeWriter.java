package com.dawid.typepython.cpp.code;

import com.dawid.typepython.symtab.symbol.TypedSymbol;

/**
 * Created by Dawid on 05.10.2019 at 17:13.
 */
public class LibraryConsoleCodeWriter extends ConsoleCodeWriter {
    private StringBuilder headerFile;
    private StringBuilder headerFileIncludeSection;
    private String headerFileName;
    private String namespace;

    public LibraryConsoleCodeWriter(String fileName, String namespace) {
        super(fileName);
        this.namespace = namespace;
        headerFile = new StringBuilder();
        headerFileIncludeSection = new StringBuilder();
        headerFile.append("#pragma once\n");
        headerFile.append("using namespace std;\n");
        headerFile.append("namespace ").append(namespace).append(" {\n");
        headerFileName = fileName + ".h";
        super.writeInclude("#include " + "\"" + headerFileName + "\"\n");
    }

    @Override
    public void writeInclude(String include) {
        super.writeInclude(include);
        headerFileIncludeSection.append(include).append("\n");
    }

    @Override
    public void writeStartMain() {
        main.append("int ").append(namespace).append("::").append(namespace).append("() {\n");
        headerFile.append("int ").append(namespace).append("();\n");
    }

    @Override
    public void writeFunctionDeclaration(String functionReturnType, String functionIdentifier) {
        super.writeFunctionDeclaration(functionReturnType, functionIdentifier);
        headerFile.append(functionReturnType).append(" ").append(functionIdentifier);
    }

    @Override
    public void writeGlobalVariableDeclaration(TypedSymbol assignable, TypedSymbol symbol) {
        global.append(assignable.getCppNameType()).append(" ").append(namespace).append("::").append(assignable.getDisplayText()).append(";");
        headerFile.append("extern ").append(assignable.getCppNameType()).append(" ").append(assignable.getDisplayText()).append(";\n");
        cursor.append(namespace).append("::").append(assignable.getDisplayText()).append(" = ").append(symbol.getDisplayText()).append(";");
    }

    @Override
    public void writeFunctionParameters(String functionParameters) {
        super.writeFunctionParameters(functionParameters);
        headerFile.append(functionParameters).append(";\n");
    }


    @Override
    public void finish() {
        super.finish();
        StringBuilder all = new StringBuilder();
        all.append(headerFileIncludeSection);
        headerFile.append("}");
        System.out.println(headerFileName);
        all.append(headerFile);
        System.out.println(all.toString());
    }
}
