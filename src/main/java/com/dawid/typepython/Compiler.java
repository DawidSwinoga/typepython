package com.dawid.typepython;

import java.io.IOException;
import java.io.InputStream;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.ConsoleCodeWriter;
import com.dawid.typepython.generated.TypePythonLexer;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.generated.TypePythonParser.FileInputContext;
import com.dawid.typepython.symtab.scope.GlobalScope;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;

public class Compiler {
    static void compile(String filePath, ConsoleCodeWriter codeWriter) throws IOException {
        InputStream inputFile = Main.class.getResourceAsStream(filePath);
        CharStream inputStream = CharStreams.fromStream(inputFile);
        TypePythonLexer typePythonLexer = new TokenTypePythonLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);

        typePythonParser.addErrorListener(new DiagnosticErrorListener());
        typePythonParser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        FileInputContext fileInputContext = typePythonParser.fileInput();

        codeWriter.writeInclude("#include <iostream>");
        codeWriter.writeInclude("#include <cmath>");
        codeWriter.writeInclude("#include <vector>");
        codeWriter.writeNamespace("using namespace std;");
        TypePythonVisitor visitor = new TypePythonVisitor(codeWriter, new GlobalScope());
        visitor.visit(fileInputContext);
        codeWriter.finish();
    }
}