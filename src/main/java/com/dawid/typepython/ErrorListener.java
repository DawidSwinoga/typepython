package com.dawid.typepython;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Created by Dawid on 02.11.2019 at 23:14.
 */
public class ErrorListener extends BaseErrorListener {
    private final BaseErrorListener errorListener;

    public ErrorListener() {
        this.errorListener = ConsoleErrorListener.INSTANCE;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
       errorListener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine + 1, msg, e);
    }
}
