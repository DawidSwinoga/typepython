package com.dawid.typepython;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;
import java.util.Stack;

public class TokenTypePythonLexer extends com.dawid.typepython.generated.TypePythonLexer {
    private LinkedList<Token> tokens = new java.util.LinkedList<>();
    private Stack<Integer> indents = new java.util.Stack<>();
    private Token lastToken = null;

    public TokenTypePythonLexer(CharStream input) {
        super(input);
    }


    @Override
    public void emit(Token t) {
        super.setToken(t);
        tokens.offer(t);
    }

    @Override
    public Token nextToken() {
        if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
            removeEOFWhenDedendsAreExpected();

            this.emit(createNewline(com.dawid.typepython.generated.TypePythonParser.NEWLINE, "\n"));
            while (!indents.isEmpty()) {
                this.emit(createDedent());
                indents.pop();
            }
            this.emit(createToken(com.dawid.typepython.generated.TypePythonParser.EOF, "<EOF>"));
        }

        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            this.lastToken = next;
        }

        return tokens.isEmpty() ? next : tokens.poll();
    }

    private void removeEOFWhenDedendsAreExpected() {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getType() == EOF) {
                tokens.remove(i);
            }
        }
    }

    private CommonToken createNewline(int newline, String s) {
        return createToken(newline, s);
    }

    private Token createDedent() {
        CommonToken dedent = createToken(com.dawid.typepython.generated.TypePythonParser.DEDENT, "");
        dedent.setLine(this.lastToken.getLine());
        return dedent;
    }

    private CommonToken createToken(int type, String text) {
        int stop = this.getCharIndex() - 1;
        int start = text.isEmpty() ? stop : stop - text.length() + 1;
        return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
    }
    static int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            if (ch == '\t') {
                count += 8 - (count % 8);
            } else {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean atStartOfInput() {
        return super.getCharPositionInLine() == 0 && super.getLine() == 1;
    }

    @Override
    public void handleNewLine() {
        String newLine = getText().replaceAll("[^\r\n\f]+", "");
        String spaces = getText().replaceAll("[\r\n\f]+", "");
        int next = _input.LA(1);
        if (next == '\r' || next == '\n' || next == '\f' || next == '#') {
            skip();
        }
        else {
            emit(createToken(NEWLINE, newLine));
            int indent = getIndentationCount(spaces);
            int previous = indents.isEmpty() ? 0 : indents.peek();
            if (indent == previous) {
                skip();
            }
            else if (indent > previous) {
                indents.push(indent);
                emit(createIndent(spaces));
            }
            else {
                while(!indents.isEmpty() && indents.peek() > indent) {
                    this.emit(createDedent());
                    indents.pop();
                }
            }
        }
    }

    private CommonToken createIndent(String spaces) {
        return createToken(com.dawid.typepython.generated.TypePythonParser.INDENT, spaces);
    }
}
