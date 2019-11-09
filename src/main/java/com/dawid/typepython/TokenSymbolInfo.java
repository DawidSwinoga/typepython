package com.dawid.typepython;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Optional;

/**
 * Created by Dawid on 04.11.2019 at 19:24.
 */

public class TokenSymbolInfo {
    private String sourceText;
    private Integer startToken;
    private Integer stopToken;
    private Integer lineNumber;


    public TokenSymbolInfo(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public TokenSymbolInfo(Integer startToken, Integer stopToken, Integer lineNumber) {
        this.startToken = startToken;
        this.stopToken = stopToken;
        this.lineNumber = lineNumber;
    }

    public TokenSymbolInfo(ParserRuleContext context) {
//        this.startToken = context.getStart().getCharPositionInLine() + 1;
//        this.stopToken = startToken + context.getText().length();
        this.sourceText = context.getText();
        this.lineNumber = context.getStart().getLine();
    }

    public Optional<Integer> getStartToken() {
        return Optional.ofNullable(startToken);
    }

    public Optional<Integer> getStopToken() {
        return Optional.ofNullable(stopToken);
    }

    public Optional<Integer> getTokenLine() {
        return Optional.ofNullable(lineNumber);
    }

    public String getSourceText() {
        return Optional.ofNullable(sourceText).orElse("");
    }

}
