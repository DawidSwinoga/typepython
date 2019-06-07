package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;

public class TypePythonVisitor extends com.dawid.typepython.generated.TypePythonBaseVisitor<Void> {
    @Override
    public Void visitFileInput(com.dawid.typepython.generated.TypePythonParser.FileInputContext ctx) {
        CodeWriter.INSTANCE.appendMainCode("int main() {");
        Void v = super.visit(ctx);
        CodeWriter.INSTANCE.appendMainCode("return 0; }");
        return v;
    }
}
