package com.dawid.typepython;

import java.io.IOException;
import java.io.InputStream;

import com.dawid.typepython.cpp.code.ConsoleCodeWriter;
import com.dawid.typepython.generated.TypePythonLexer;
import com.dawid.typepython.generated.TypePythonParser;
import com.dawid.typepython.generated.TypePythonParser.FileInputContext;
import com.dawid.typepython.symtab.scope.GlobalScope;
import com.dawid.typepython.symtab.scope.Scope;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;

public class Compiler {
    static Scope compile(String filePath, ConsoleCodeWriter codeWriter, GlobalScope scope, TokenSymbolInfo tokenSymbolInfo) {
        InputStream inputFile = Main.class.getResourceAsStream(filePath);
        if (inputFile == null) {
            throw new FileNotFoundException(filePath, tokenSymbolInfo);
        }
        try {
            return tryCompile(filePath, codeWriter, scope, tokenSymbolInfo);
        } catch (CompilerException exception) {
            if (Main.DEBUG) {
                System.err.println(filePath + ":" + exception.getCompilerError());
                exception.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(1);
        return null;
    }

    private static Scope tryCompile(String filePath, ConsoleCodeWriter codeWriter, GlobalScope scope, TokenSymbolInfo tokenSymbolInfo) {
        InputStream inputFile = Main.class.getResourceAsStream(filePath);
        CharStream inputStream;

        try {
            inputStream = CharStreams.fromStream(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TypePythonLexer typePythonLexer = new TokenTypePythonLexer(inputStream);
        typePythonLexer.removeErrorListeners();
        typePythonLexer.addErrorListener(new ErrorListener());
        typePythonLexer.addErrorListener(new DiagnosticErrorListener());
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);

        typePythonParser.removeErrorListeners();
        typePythonParser.addErrorListener(new ErrorListener());
        typePythonParser.addErrorListener(new DiagnosticErrorListener());
        typePythonParser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        FileInputContext fileInputContext = typePythonParser.fileInput();

        codeWriter.writeInclude("#include <iostream>");
        codeWriter.writeInclude("#include <cmath>");
        codeWriter.writeInclude("#include <vector>");
        codeWriter.writeInclude("#include <set>");
        codeWriter.writeInclude("#include <map>");
        codeWriter.writeInclude("#include \"stdtpy/stdtpy.h\"");
        codeWriter.writeNamespace("using namespace std;");
        TypePythonVisitor visitor = new TypePythonVisitor(codeWriter, scope);
        visitor.visit(fileInputContext);
        codeWriter.finish();
        return visitor.getCurrentScope();
    }
}