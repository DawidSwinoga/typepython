package com.dawid.typepython;

/**
 * Created by Dawid on 04.11.2019 at 18:50.
 */
public class CompilerException extends RuntimeException {
    private TokenSymbolInfo tokenSymbolInfo;

    public CompilerException(TokenSymbolInfo tokenSymbolInfo) {
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    public CompilerException(String message, TokenSymbolInfo tokenSymbolInfo) {
        super(message);
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    public CompilerException(String message, Throwable cause, TokenSymbolInfo tokenSymbolInfo) {
        super(message, cause);
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    public CompilerException(Throwable cause, TokenSymbolInfo tokenSymbolInfo) {
        super(cause);
        this.tokenSymbolInfo = tokenSymbolInfo;
    }

    protected String errorPosition() {
        if (tokenSymbolInfo == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        tokenSymbolInfo.getTokenLine().ifPresent(it -> stringBuilder.append(it).append(":"));
        tokenSymbolInfo.getStartToken().ifPresent(stringBuilder::append);
        tokenSymbolInfo.getStopToken().ifPresent(it -> stringBuilder.append("-").append(it));
        stringBuilder.append(" error: ");
        return stringBuilder.toString();
    }

    public String getCompilerError() {
        return errorPosition() + getMessage();
    }
}
