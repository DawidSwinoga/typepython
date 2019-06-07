package com.dawid.typepython;

import com.dawid.typepython.cpp.code.CodeWriter;
import com.dawid.typepython.generated.TypePythonLexer;
import com.dawid.typepython.generated.TypePythonParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Dawid on 18.05.2019 at 15:41.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        CharStream inputStream = CharStreams.fromPath(Paths.get("/home/dawid/IdeaProjects/typepython/src/main/resources/test.typepython"));
        com.dawid.typepython.generated.TypePythonLexer typePythonLexer = new TypePythonLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(typePythonLexer);

        com.dawid.typepython.generated.TypePythonParser typePythonParser = new TypePythonParser(commonTokenStream);
        TypePythonParser.File_inputContext file_inputContext = typePythonParser.file_input();
        TypePythonVisitor visitor = new TypePythonVisitor();
        visitor.visit(file_inputContext);
        CodeWriter.INSTANCE.writeAll();
    }
}
