package com.dawid.typepython;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Created by Dawid on 02.11.2019 at 23:14.
 */
public class ErrorListener extends BaseErrorListener {
    private final String fileName;

    public ErrorListener(String fileName) {
        this.fileName = fileName;
    }


    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
       System.err.println(fileName + ": line " + line + ":" + (charPositionInLine) + " " + msg);
    }
}
