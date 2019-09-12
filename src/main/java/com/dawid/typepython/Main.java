package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.cpp.code.ConsoleCodeWriter;
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
//        InputStream inputFile = Main.class.getResourceAsStream("/bubble_sort.tpy");
//        InputStream inputFile = Main.class.getResourceAsStream("/prime_numbers.tpy");
//        InputStream inputFile = Main.class.getResourceAsStream("/factorial.tpy");
        InputStream inputFile = Main.class.getResourceAsStream("/test.tpy");
        CharStream inputStream = CharStreams.fromStream(inputFile);
        com.dawid.typepython.generated.TypePythonLexer typePythonLexer = new TokenTypePythonLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        com.dawid.typepython.generated.TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);

        typePythonParser.addErrorListener(new DiagnosticErrorListener());
        typePythonParser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        TypePythonParser.FileInputContext fileInputContext = typePythonParser.fileInput();

        CodeWriter codeWriter = new ConsoleCodeWriter();
        codeWriter.writeInclude("#include <iostream>");
        codeWriter.writeInclude("#include <cmath>");
        codeWriter.writeInclude("#include <vector>");
        codeWriter.writeNamespace("using namespace std;");
        TypePythonVisitor visitor = new TypePythonVisitor(codeWriter);
        visitor.visit(fileInputContext);
        codeWriter.finish();
    }
}
