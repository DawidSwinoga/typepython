package com.dawid.typepython;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Compiler {

    public static final String STD_LIBRARY_LOCATION = "stdtpy/stdtpy.h";
    public static final String CPP_INCLUDE = "#include \"";

    static Scope compile(String filePath, ConsoleCodeWriter codeWriter, GlobalScope scope, TokenSymbolInfo tokenSymbolInfo) {
        if (!(new File(FileContext.getRootPath() + filePath).exists())) {
            throw new FileNotFoundException(filePath, tokenSymbolInfo);
        }
        try {
            return tryCompile(filePath, codeWriter, scope);
        } catch (CompilerException exception) {
            System.err.println(filePath + ":" + exception.getCompilerError());
            if (Main.DEBUG) {
                exception.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(1);
        return null;
    }

    private static Scope tryCompile(String filePath, ConsoleCodeWriter codeWriter, GlobalScope scope) {
        InputStream inputFile;
        try {
            inputFile = new FileInputStream(FileContext.getRootPath() + filePath);
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        CharStream inputStream;


        try {
            inputStream = CharStreams.fromStream(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TypePythonLexer typePythonLexer = new TokenTypePythonLexer(inputStream);
        typePythonLexer.removeErrorListeners();
        typePythonLexer.addErrorListener(new ErrorListener(filePath));
        typePythonLexer.addErrorListener(new DiagnosticErrorListener());
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);

        typePythonParser.removeErrorListeners();
        typePythonParser.addErrorListener(new ErrorListener(filePath));
        typePythonParser.addErrorListener(new DiagnosticErrorListener());
        typePythonParser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        FileInputContext fileInputContext = typePythonParser.fileInput();

        String pathRelative = PathUtils.getRelativePath(STD_LIBRARY_LOCATION, filePath);
        codeWriter.writeInclude(CPP_INCLUDE + pathRelative + "\"");
        TypePythonVisitor visitor = new TypePythonVisitor(codeWriter, scope);
        visitor.visit(fileInputContext);
        codeWriter.finish();
        return visitor.getCurrentScope();
    }
}