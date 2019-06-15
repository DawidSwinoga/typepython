package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.InMemoryCodeWriter;
import com.dawid.typepython.generated.TypePythonParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dawid on 18.05.2019 at 15:41.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        InputStream inputFile = Main.class.getResourceAsStream("/test.typepython");
        CharStream inputStream = CharStreams.fromStream(inputFile);
//        ANTLRInputStream inputStream = new ANTLRInputStream("adsa = 1\n");
        com.dawid.typepython.generated.TypePythonLexer typePythonLexer = new TokenTypePythonLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        com.dawid.typepython.generated.TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);

        typePythonParser.addErrorListener(new DiagnosticErrorListener());
        typePythonParser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        TypePythonParser.FileInputContext fileInputContext = typePythonParser.fileInput();

        CodeWriter codeWriter = new InMemoryCodeWriter();
        codeWriter.writeInclude("#include <iostream>");
        codeWriter.writeNamespace("using namespace std;");
        TypePythonVisitor visitor = new TypePythonVisitor(codeWriter);
        visitor.visit(fileInputContext);
        codeWriter.finish();
    }
}
