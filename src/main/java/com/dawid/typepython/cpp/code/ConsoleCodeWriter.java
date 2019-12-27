package com.dawid.typepython.cpp.code;

import com.dawid.typepython.WriterFactory;
import com.dawid.typepython.symtab.scope.Scope;
import com.dawid.typepython.symtab.symbol.TypedSymbol;
import lombok.Getter;

import static com.dawid.typepython.TypePythonVisitor.TYPE_PYTHON_FILE_EXTENSION;

@Getter
public class ConsoleCodeWriter implements CodeWriter {
    public static final String CPP_FILE_EXTENSION = ".cpp";
    protected StringBuilder global;
    protected StringBuilder main;
    private StringBuilder namespace;
    private StringBuilder include;
    private StringBuilder functionDeclaration;
    private Scope scope;
    protected StringBuilder cursor;
    private String fileName;
    protected final Writer writer;

    public ConsoleCodeWriter(String fileName) {
        this.fileName = generateFileName(fileName);
        this.writer = WriterFactory.create(this.fileName);
        this.namespace = new StringBuilder();
        this.include = new StringBuilder();
        this.global = new StringBuilder();
        this.main = new StringBuilder();
        this.functionDeclaration = new StringBuilder();
        this.cursor = main;
    }

    private String generateFileName(String fileName) {
        if (fileName.endsWith(TYPE_PYTHON_FILE_EXTENSION)) {
            return fileName.replace(TYPE_PYTHON_FILE_EXTENSION, CPP_FILE_EXTENSION);
        } else {
            return fileName + ".cpp";
        }
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
        writer.write(fileName);
        writer.write(code.replaceAll(";", ";\n"));
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
        if (!assignable.isDeclaredInScope() && !scope.isLocalScope() && !assignable.isTemporary()) {
            writeGlobalVariableDeclaration(assignable, symbol);
        }

        if (assignable.isDeclaredInScope()) {
            cursor.append(assignable.getDisplayText()).append(" = ").append(symbol.getDisplayText()).append(";");
        } else if (scope.isLocalScope() || assignable.isTemporary()) {
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
