package com.dawid.typepython.cpp.code;

import com.dawid.typepython.symtab.Scope;
import com.dawid.typepython.symtab.symbol.VariableSymbol;

public class InMemoryCodeWriter implements CodeWriter {
    private StringBuilder global;
    private StringBuilder main;
    private StringBuilder namespace;
    private StringBuilder include;
    private Scope scope;
    private StringBuilder cursor;

    public InMemoryCodeWriter() {
        this.namespace = new StringBuilder();
        this.include = new StringBuilder();
        this.global = new StringBuilder();
        this.main = new StringBuilder();
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
        all.append(main);
        String code = all.toString();
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
    public void writeAssignment(VariableSymbol assignable, VariableSymbol symbol) {
        if (!assignable.isDeclaredInScope() && !scope.isLocalScope()) {
            global.append(assignable.getTypeName()).append(" ").append(assignable.getText()).append(";");
            cursor.append(assignable.getText()).append(" = ").append(symbol.getText()).append(";");
        }

        if (assignable.isDeclaredInScope()) {
            cursor.append(assignable.getText()).append(" = ").append(symbol.getText()).append(";");
        } else if (scope.isLocalScope()) {
            cursor.append(assignable.getTypeName()).append(" ").append(assignable.getText()).append(" = ").append(symbol.getText()).append(";");
        }
    }
}
