grammar TypePython;

@header {
package com.dawid.typepython.generated;
}

tokens { INDENT, DEDENT }


@lexer::members {
  // A queue where extra tokens are pushed on (see the NEWLINE lexer rule).
  private java.util.LinkedList<Token> tokens = new java.util.LinkedList<>();
  // The stack that keeps track of the indentation level.
  private java.util.Stack<Integer> indents = new java.util.Stack<>();
  // The amount of opened braces, brackets and parenthesis.
  private int opened = 0;
  // The most recently produced token.
  private Token lastToken = null;
  @Override
  public void emit(Token t) {
    super.setToken(t);
    tokens.offer(t);
  }

  @Override
  public Token nextToken() {
    // Check if the end-of-file is ahead and there are still some DEDENTS expected.
    if (_input.LA(1) == EOF && !this.indents.isEmpty()) {
      // Remove any trailing EOF tokens from our buffer.
      for (int i = tokens.size() - 1; i >= 0; i--) {
        if (tokens.get(i).getType() == EOF) {
          tokens.remove(i);
        }
      }

      // First emit an extra line break that serves as the end of the statement.
      this.emit(commonToken(TypePythonParser.NEWLINE, "\n"));

      // Now emit as much DEDENT tokens as needed.
      while (!indents.isEmpty()) {
        this.emit(createDedent());
        indents.pop();
      }

      // Put the EOF back on the token stream.
      this.emit(commonToken(TypePythonParser.EOF, "<EOF>"));
    }

    Token next = super.nextToken();

    if (next.getChannel() == Token.DEFAULT_CHANNEL) {
      // Keep track of the last token on the default channel.
      this.lastToken = next;
    }

    return tokens.isEmpty() ? next : tokens.poll();
  }

  private Token createDedent() {
    CommonToken dedent = commonToken(TypePythonParser.DEDENT, "");
    dedent.setLine(this.lastToken.getLine());
    return dedent;
  }

  private CommonToken commonToken(int type, String text) {
    int stop = this.getCharIndex() - 1;
    int start = text.isEmpty() ? stop : stop - text.length() + 1;
    return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
  }

  // Calculates the indentation of the provided spaces, taking the
  // following rules into account:
  //
  // "Tabs are replaced (from left to right) by one to eight spaces
  //  such that the total number of characters up to and including
  //  the replacement is a multiple of eight [...]"
  //
  //  -- https://docs.python.org/3.1/reference/lexical_analysis.html#indentation
  static int getIndentationCount(String spaces) {
    int count = 0;
    for (char ch : spaces.toCharArray()) {
      switch (ch) {
        case '\t':
          count += 8 - (count % 8);
          break;
        default:
          // A normal space char.
          count++;
      }
    }

    return count;
  }

  boolean atStartOfInput() {
    return super.getCharPositionInLine() == 0 && super.getLine() == 1;
  }
}

fileInput
    : (NEWLINE | statement)* EOF
    ;

funcDefinition
    : DEF IDENTIFIER parameters ':' suite
    ;
suite
    : NEWLINE INDENT statement+ DEDENT
    ;

parameters
    : '(' (typeDeclarationArgsList)? ')'
    ;

typeDeclarationArgsList
    : variableDeclaration (',' variableDeclaration)*
    ;
variableDeclaration
    : IDENTIFIER (':' type)?
    ;

type
    :   basicType
    |   customType
    ;

basicType
    : BOOLEAN
    | DOUBLE
    | FLOAT
    | INTEGER
    | LONG
    ;

customType
    : IDENTIFIER
    ;

statement
    : simpleStatement
    | compoundStatement
    ;

simpleStatement
    : smallStatement NEWLINE
    ;
smallStatement
    : expressionStatement
    | passStatement
    | flowStatement
    | importStatement
    ;

expressionStatement
    : assignable '=' test
    ;

passStatement
    : 'pass'
    ;

flowStatement
    : returnStatement
    ;

returnStatement
    : 'return' test
    ;

importStatement
    : 'import' IDENTIFIER
    ;

assignable
    : declaration       #assignableDeclaration
    | IDENTIFIER        #assignableIdentifier
    ;

declaration
    : IDENTIFIER ':' type
    ;


compoundStatement
    : ifStatement
    | whileStatement
    | forStatement
    | funcDefinition
    ;

ifStatement: 'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?;
whileStatement: 'while' test ':' suite;
forStatement: 'for' IDENTIFIER 'in' IDENTIFIER ':' suite;

test: orStatement;
orStatement: andStatement ('or' andStatement)*;
andStatement: notTest ('and' notTest)*;
notTest: 'not' notTest | comparison;
comparison: expr (compareOperator expr)*;

compareOperator: '<'|'>'|'=='|'<='|'>='|'not'|'!=';
expr: term (('+'|'-') term)*;
term: factor (('*'|'/'|'%') factor)*;
factor: ('+'|'-') factor | atomExpression;
atomExpression: atom trailer*;


atom
    : '(' (arguments)? ')'
    | '[' (arguments)? ')'
    | '{' (dictorySetMakers)? '}'
    | IDENTIFIER
    | literal
    ;

arguments: argument (',' argument)*;
argument: test;

dictorySetMakers
    : dictorySetMaker (',' dictorySetMaker)*
    ;

dictorySetMaker
    : test ':' test
    ;

trailer: '(' (arguments)? ')' | '[' argument ']' | '.' IDENTIFIER;

literal
    : INTEGER_LITERAL       #integerLiteral
    | FLOAT_LITERAL         #floatLiteral
    | DOUBLE_LITERAL        #doubleLiteral
    | LONG_LITERAL          #longLiteral
    | STRING_LITERAL        #stringLiteral
    | BOOLEAN_LITERAL       #booleanLiteral
    ;

IF                      : 'if';
ELSE                    : 'else';
BREAK                   : 'break';
ELIF                    : 'elif';
FOR                     : 'for';
IN                      : 'in';
WHILE                   : 'while';
DEF                     : 'def';
RETURN                  : 'return';
PASS                    : 'pass';
IMPORT                  : 'import';
AND                     : 'and';
OR                      : 'or';
NOT                     : 'not';
ASSIGN                  : '=';
EQUAL                   : '==';
NOT_EQUAL               : '!=';
GREATHER_THEN           : '>';
GREATHER_EQUEAL_THEN    : '>=';
LOWER_THEN              : '<';
LOWER_EQUAL_THEN        : '<=';

LPAREN                  : '(';
RPAREN                  : ')';
LEFT_BRACE              : '[';
RIGHT_BRACE             : ']';
COMA                    : '.';

BOOLEAN_LITERAL         : 'true'
                        | 'false'
                        ;

BOOLEAN                 : 'boolean';
FLOAT                   : 'float';
DOUBLE                  : 'double';
INTEGER                 : 'integer';
LONG                    : 'long';

IDENTIFIER              : IDENTIFIER_START IDENTIFIER_CONTINUE*;

STRING_LITERAL          : '"' .*? '"';

INTEGER_LITERAL         : DECIMAL_NUMERAL;
LONG_LITERAL            : DECIMAL_NUMERAL  LONG_TYPE_SUFFIX;
FLOAT_LITERAL           : DECIMAL_FLOATING_POINT_LITERAL;
DOUBLE_LITERAL          : DECIMAL_FLOATING_POINT_LITERAL DOUBLE_TYPE_SUFFIX;

NEWLINE
    : ( {atStartOfInput()}?   SPACES
         | ( '\r'? '\n' | '\r' | '\f' ) SPACES?
         )
         {
           String newLine = getText().replaceAll("[^\r\n\f]+", "");
           String spaces = getText().replaceAll("[\r\n\f]+", "");
           int next = _input.LA(1);
           if (opened > 0 || next == '\r' || next == '\n' || next == '\f' || next == '#') {
             // If we're inside a list or on a blank line, ignore all indents,
             // dedents and line breaks.
             skip();
           }
           else {
             emit(commonToken(NEWLINE, newLine));
             int indent = getIndentationCount(spaces);
             int previous = indents.isEmpty() ? 0 : indents.peek();
             if (indent == previous) {
               // skip indents of the same size as the present indent-size
               skip();
             }
             else if (indent > previous) {
               indents.push(indent);
               emit(commonToken(TypePythonParser.INDENT, spaces));
             }
             else {
               // Possibly emit more than 1 DEDENT token.
               while(!indents.isEmpty() && indents.peek() > indent) {
                 this.emit(createDedent());
                 indents.pop();
               }
             }
           }
         }
       ;

SKIP_
 : ( SPACES ) -> skip
 ;

fragment SPACES
 : [ \t]+
 ;

fragment DECIMAL_NUMERAL    : '0'
                            | NON_ZERO_DIGIT DIGITS?
                            ;



fragment DIGIT          : '0'
                        | NON_ZERO_DIGIT
                        ;

fragment DIGITS         : DIGIT+;

fragment NON_ZERO_DIGIT : [1-9];

fragment LONG_TYPE_SUFFIX   : 'L';

fragment DOUBLE_TYPE_SUFFIX : 'd';

fragment DECIMAL_FLOATING_POINT_LITERAL : DIGIT '.' DIGITS;

fragment IDENTIFIER_START   : '_'
                            | [a-zA-Z]
                            ;

fragment IDENTIFIER_CONTINUE : IDENTIFIER_START
                              | [0-9]
                              ;