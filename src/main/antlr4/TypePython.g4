grammar TypePython;

@header {
package com.dawid.typepython.generated;
}

tokens { INDENT, DEDENT }


@lexer::members {
    public void handleNewLine() {}
    public boolean atStartOfInput() { return false; }
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
    : IDENTIFIER ':' type
    ;

type
    : genericType
    | simpleType
    ;

genericType
    : IDENTIFIER '<' type '>'
    ;

simpleType
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
    | executeStatement
    | passStatement
    | flowStatement
    | importStatement
    ;

executeStatement
    : atom trailer+
    ;

expressionStatement
    : assignable '=' test       #assignableExpressionStatement
    | variableDeclaration       #variableDeclarationStatement
    ;

passStatement
    : 'pass'
    ;

flowStatement
    : returnStatement
    | breakStatement
    ;

breakStatement
    : BREAK
    ;

returnStatement
    : 'return' test
    ;

importStatement
    : 'import' IDENTIFIER
    ;

assignable
    : variableDeclaration       #assignableDeclaration
    | IDENTIFIER                #assignableIdentifier
    ;


compoundStatement
    : ifStatement
    | whileStatement
    | forStatement
    | funcDefinition
    ;

ifStatement: 'if' test ':' suite elifStatement* elseStatement?;
elifStatement: 'elif' test ':' suite;
elseStatement: 'else' ':' suite;

whileStatement: 'while' test ':' suite;
forStatement: 'for' IDENTIFIER 'in' IDENTIFIER ':' suite;

test: conditionalOrStatement;
conditionalOrStatement
    : conditionalAndStatement      #notOrStatement
    | left=conditionalOrStatement operator='or' right=conditionalAndStatement  #orStatement
    ;
conditionalAndStatement
    : notTest #notAndStatement
    | left=conditionalAndStatement operator='and' right=notTest #andStatement
    ;
notTest
    : 'not' notTest #negationTest
    | comparison   #comparisionNotTest
    ;
comparison: expr (compareOperator expr)*;

compareOperator: '<'|'>'|'=='|'<='|'>='|'not'|'!=';
expr
    : term      #termExpression
    | expr operator=('+'|'-') term  #additiveExpression
    ;
term
    : factor #factorTerm
    | term operator=('*'|'/'|'%') factor #multiplicativeExpression
    ;
factor
    : sign=('+'|'-') factor #signFactor
    | conditionalPower #conditioanlPowerFactor
    ;
conditionalPower: atomExpression ('**' exponent=factor)?;
atomExpression: atom trailer*;


atom
    : '(' (arguments)? ')'          #conditionalTupleAtom
    | '[' (arguments)? ']'          #listAtom
    | '{' (dictorySetMakers)? '}'   #dictorySetMakersAtom
    | literal                       #literalAtom
    | IDENTIFIER                    #identifierAtom
    ;

arguments: first=argument (',' argument)*;
argument: test;

dictorySetMakers
    : dictorySetMaker (',' dictorySetMaker)*
    ;

dictorySetMaker
    : test ':' test
    ;

trailer
    : '(' (arguments)? ')'
    | '[' argument ']'
    | '.' IDENTIFIER;

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
GREATER_THEN           : '>';
GREATER_THEN_OR_EQUAL_TO    : '>=';
LOWER_THEN              : '<';
LOWER_THEN_OR_EQUAL_TO        : '<=';

LPAREN                  : '(';
RPAREN                  : ')';
LEFT_BRACE              : '[';
RIGHT_BRACE             : ']';
DOT                     : '.';

BOOLEAN_LITERAL         : 'True'
                        | 'False'
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
FLOAT_LITERAL           : DECIMAL_FLOATING_POINT_LITERAL FLOAT_TYPE_SUFFIX;
DOUBLE_LITERAL          : DECIMAL_FLOATING_POINT_LITERAL;

NEWLINE
    : ( {atStartOfInput()}?   SPACES
         | ( '\r'? '\n' | '\r' | '\f' ) SPACES?
         )
         {
           handleNewLine();
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

fragment LONG_TYPE_SUFFIX   : 'L' | 'l';

fragment FLOAT_TYPE_SUFFIX : 'f' | 'F';

fragment DECIMAL_FLOATING_POINT_LITERAL : DECIMAL_NUMERAL DOT DIGITS;

fragment IDENTIFIER_START   : '_'
                            | [a-zA-Z]
                            ;

fragment IDENTIFIER_CONTINUE : IDENTIFIER_START
                              | [0-9]
                              ;