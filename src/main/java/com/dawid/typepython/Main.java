package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.generated.TypePythonLexer;
import com.dawid.typepython.generated.TypePythonParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Dawid on 18.05.2019 at 15:41.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        CharStream inputStream = CharStreams.fromPath(Paths.get("D:\\programy\\typepython\\src\\main\\resources\\test.typepython"));
//        ANTLRInputStream inputStream = new ANTLRInputStream("adsa = 1\n");
        com.dawid.typepython.generated.TypePythonLexer typePythonLexer = new TypePythonLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        com.dawid.typepython.generated.TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);

        typePythonParser.addErrorListener(new DiagnosticErrorListener());
        typePythonParser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        TypePythonParser.FileInputContext fileInputContext = typePythonParser.fileInput();
        TypePythonVisitor visitor = new TypePythonVisitor();
        visitor.visit(fileInputContext);
        CodeWriter.INSTANCE.writeAll();
    }
}
