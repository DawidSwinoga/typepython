package com.dawid.typepython.cpp.code;

import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import lombok.Getter;

@Getter
public class ConsoleCodeWriter implements CodeWriter {
    protected StringBuilder global;
    protected StringBuilder main;
    private StringBuilder namespace;
    private StringBuilder include;
    private StringBuilder functionDeclaration;
    private Scope scope;
    protected StringBuilder cursor;
    private String fileName;

    public ConsoleCodeWriter(String fileName) {
        this.fileName = fileName + ".cpp";
        this.namespace = new StringBuilder();
        this.include = new StringBuilder();
        this.global = new StringBuilder();
        this.main = new StringBuilder();
        this.functionDeclaration = new StringBuilder();
        this.cursor = main;
    }

    @Override
    public void writeInclude(String include) {
        this.include.append(include).append("\n");
    }

    @Override
    public void writeNamespace(String namespace) {
        this.namespace.append(namespace).append("\n");
    }

    @Override
    public void writeGlobal(String code) {
        global.append(code);
    }

    @Override
    public void writeMain(String code) {
        main.append(code);
    }


    @Override
    public void finish() {
        StringBuilder all = new StringBuilder();
        all.append(include);
        all.append("\n");
        all.append(namespace);
        all.append("\n");
        all.append(global);
        all.append("\n");
        all.append(functionDeclaration);
        all.append("\n");
        all.append(main);
        String code = all.toString();
        System.out.println(fileName);
        System.out.println(code.replaceAll(";", ";\n"));
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void writeStartMain() {
        main.append("int main() {\n");
    }

    @Override
    public void writeEndMain() {
        main.append("return 0;\n }");
    }

    @Override
    public void startScope() {
        cursor.append("{").append("\n");
    }

    @Override
    public void endScope() {
        cursor.append("}").append("\n");
    }

    @Override
    public void write(String code) {
        cursor.append(code);
    }

    @Override
    public void writeFunctionParameters(String functionParameters) {
        cursor.append(functionParameters);
    }

    @Override
    public void writeFunctionDeclaration(String functionReturnType, String functionIdentifier) {
        String namespacePrefix = scope.getNamespace().map(it -> it + "::").orElse("");
        cursor.append(functionReturnType).append(" ").append(namespacePrefix).append(functionIdentifier);
    }

    @Override
    public void startFunction() {
        cursor = functionDeclaration;
    }

    @Override
    public void endFunction() {
        cursor = main;
    }

    @Override
    public void writeAssignment(TypedSymbol assignable, TypedSymbol symbol) {
        if (!assignable.isDeclaredInScope() && !scope.isLocalScope()) {
            writeGlobalVariableDeclaration(assignable, symbol);
        }

        if (assignable.isDeclaredInScope()) {
            cursor.append(assignable.getDisplayText()).append(" = ").append(symbol.getDisplayText()).append(";");
        } else if (scope.isLocalScope()) {
            cursor.append(assignable.getCppNameType()).append(" ").append(assignable.getDisplayText()).append(" = ").append(symbol.getDisplayText()).append(";");
        }
    }

    @Override
    public void writeDeclaration(TypedSymbol assignable) {
        if (!assignable.isDeclaredInScope() && !scope.isLocalScope()) {
            global.append(assignable.getCppNameType()).append(" ").append(assignable.getDisplayText()).append(";");
        }

        if (scope.isLocalScope()) {
            cursor.append(assignable.getCppNameType()).append(";");
        }
    }

    @Override
    public void writeGlobalVariableDeclaration(TypedSymbol assignable, TypedSymbol symbol) {
        global.append(assignable.getCppNameType()).append(" ").append(assignable.getDisplayText()).append(";");
        cursor.append(assignable.getDisplayText()).append(" = ").append(symbol.getDisplayText()).append(";");
    }
}
