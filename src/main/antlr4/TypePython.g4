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
    : (NEWLINE | importStatement | funcDefinition | statement)* EOF
    ;

funcDefinition
    : DEF IDENTIFIER parameters (COLON type)? COLON suite
    ;
suite
    : NEWLINE INDENT statement+ DEDENT
    ;

parameters
    : LEFT_PAREN (typeDeclarationArgsList)? RIGHT_PAREN
    ;

typeDeclarationArgsList
    : variableDeclaration (COMMA variableDeclaration)*
    ;
variableDeclaration
    : IDENTIFIER COLON type
    ;

type
    : genericType
    | simpleType
    ;

genericType
    : IDENTIFIER LOWER_THEN type (COMMA type)* GREATER_THEN
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
    ;

executeStatement
    : atom trailer+
    ;

expressionStatement
    : assignable ASSIGN test       #assignableExpressionStatement
    | variableDeclaration       #variableDeclarationStatement
    ;

passStatement
    : PASS
    ;

flowStatement
    : returnStatement
    | breakStatement
    ;

breakStatement
    : BREAK
    ;

returnStatement
    : RETURN test?
    ;

importStatement
    : IMPORT dottedIdentifier NEWLINE
    ;

dottedIdentifier
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

assignable
    : variableDeclaration       #assignableDeclaration
    | IDENTIFIER                #assignableIdentifier
    | atomTrailer               #assignableAtomTrailer
    ;

atomTrailer
    : atom trailer+
    ;

compoundStatement
    : ifStatement
    | whileStatement
    | forStatement
    ;

ifStatement: IF test COLON suite elifStatement* elseStatement?;
elifStatement: ELIF test COLON suite;
elseStatement: ELSE COLON suite;

whileStatement: WHILE test COLON suite;
forStatement: FOR variable=IDENTIFIER IN collection=atomExpression COLON suite;

test: conditionalOrStatement;
conditionalOrStatement
    : conditionalAndStatement      #notOrStatement
    | left=conditionalOrStatement operator=OR right=conditionalAndStatement  #orStatement
    ;
conditionalAndStatement
    : notTest #notAndStatement
    | left=conditionalAndStatement operator=AND right=notTest #andStatement
    ;
notTest
    : NOT notTest #negationTest
    | comparison   #comparisionNotTest
    ;
comparison: expression (compareOperator expression)*;

compareOperator: LOWER_THEN|GREATER_THEN|EQUAL|LOWER_THEN_OR_EQUAL_TO|GREATER_THEN_OR_EQUAL_TO|NOT|NOT_EQUAL;
expression
    : term      #termExpression
    | expression operator=(ADD|MINUS) term  #additiveExpression
    ;
term
    : factor #factorTerm
    | term operator=(MUL|DIV|MOD) factor #multiplicativeExpression
    ;
factor
    : sign=(ADD|MINUS) factor #signFactor
    | conditionalPower #conditioanlPowerFactor
    ;
conditionalPower: atomExpression (POWER exponent=factor)?;
atomExpression: atom trailer*;


atom
    : LEFT_PAREN (arguments) RIGHT_PAREN           #conditionalTupleAtom
    | LEFT_BRACKET (arguments)? RIGHT_BRACKET          #listAtom
    | LEFT_BRACE (arguments) RIGHT_BRACE          #setAtom
    | LEFT_BRACE (dictionarySetMakers)? RIGHT_BRACE   #dictorySetMakersAtom
    | literal                       #literalAtom
    | IDENTIFIER                    #identifierAtom
    ;

arguments: first=argument (COMMA argument)*;
argument: test;

dictionarySetMakers
    : dictionarySetMaker (COMMA dictionarySetMaker)*
    ;

dictionarySetMaker
    : key=test COLON value=test
    ;

trailer
    : LEFT_PAREN (arguments)? RIGHT_PAREN #trailerParenthesis
    | LEFT_BRACKET argument RIGHT_BRACKET     #trailerBrackets
    | DOT IDENTIFIER      #trailerIdentifier
    ;

literal
    : INTEGER_LITERAL       #integerLiteral
    | FLOAT_LITERAL         #floatLiteral
    | DOUBLE_LITERAL        #doubleLiteral
    | LONG_LITERAL          #longLiteral
    | STRING_LITERAL        #stringLiteral
    | BOOLEAN_LITERAL       #booleanLiteral
    ;

IF                          : 'if';
ELSE                        : 'else';
BREAK                       : 'break';
ELIF                        : 'elif';
FOR                         : 'for';
IN                          : 'in';
WHILE                       : 'while';
DEF                         : 'def';
RETURN                      : 'return';
PASS                        : 'pass';
IMPORT                      : 'import';
AND                         : 'and';
OR                          : 'or';
NOT                         : 'not';
ASSIGN                      : '=';
EQUAL                       : '==';
NOT_EQUAL                   : '!=';
GREATER_THEN                : '>';
GREATER_THEN_OR_EQUAL_TO    : '>=';
LOWER_THEN                  : '<';
LOWER_THEN_OR_EQUAL_TO      : '<=';
ADD                         : '+';
MINUS                       : '-';
POWER                       : '**';
MUL                         : '*';
DIV                         : '/';
MOD                         : '%';

LEFT_PAREN                  : '(';
RIGHT_PAREN                 : ')';
LEFT_BRACKET                : '[';
RIGHT_BRACKET               : ']';
LEFT_BRACE                  : '{';
RIGHT_BRACE                 : '}';
COMMA                       : ',';
DOT                         : '.';
COLON                       : ':';

BOOLEAN_LITERAL             : 'True'
                            | 'False'
                            ;

BOOLEAN                     : 'boolean';
FLOAT                       : 'float';
DOUBLE                      : 'double';
INTEGER                     : 'integer';
LONG                        : 'long';

IDENTIFIER                  : IDENTIFIER_START IDENTIFIER_CONTINUE*;

STRING_LITERAL              : '"' .*? '"';

INTEGER_LITERAL             : DECIMAL_NUMERAL;
LONG_LITERAL                : DECIMAL_NUMERAL  LONG_TYPE_SUFFIX;
FLOAT_LITERAL               : DECIMAL_FLOATING_POINT_LITERAL FLOAT_TYPE_SUFFIX
                            | DECIMAL_NUMERAL FLOAT_TYPE_SUFFIX
                            ;
DOUBLE_LITERAL              : DECIMAL_FLOATING_POINT_LITERAL;

NEWLINE
                            : ( {atStartOfInput()}?   SPACES
                            | ( '\r'? '\n' | '\r' | '\f' ) SPACES?) {
                                handleNewLine();
                              }
                            ;

WHITE_SPACES                : ( SPACES ) -> skip;
COMMENT                     : '/*' .*? '*/' -> skip;
LINE_COMMENT                : '#' ~[\r\n]* -> skip;

fragment SPACES : [ \t]+;

fragment NON_ZERO_DIGIT : [1-9];

fragment DIGIT : '0'
               | NON_ZERO_DIGIT
               ;

fragment DIGITS : DIGIT+;

fragment DECIMAL_NUMERAL : '0'
                         | NON_ZERO_DIGIT DIGITS?
                         ;

fragment LONG_TYPE_SUFFIX   : 'L' | 'l';

fragment FLOAT_TYPE_SUFFIX : 'f' | 'F';

fragment DECIMAL_FLOATING_POINT_LITERAL : DECIMAL_NUMERAL DOT DIGITS;

fragment IDENTIFIER_START : '_'
                          | [a-zA-Z]
                          ;

fragment IDENTIFIER_CONTINUE : IDENTIFIER_START
                             | [0-9]
                             ;