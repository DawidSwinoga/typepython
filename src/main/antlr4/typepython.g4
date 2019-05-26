grammar typepython;

tokens { INDENT, DEDENT }

file_input
    : (NEWLINE | statement)* EOF
    ;

funcdef
    : DEF IDENTIFIER parameters ':' suite
    ;
suite
    : NEWLINE INDENT statement+ DEDENT
    ;

parameters
    : '(' (typedargslist)? ')'
    ;

typedargslist
    : tfpdef (',' tfpdef)*
    ;
tfpdef
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
    : simple_stmt
    | compound_stmt
    ;

simple_stmt
    : small_stmt NEWLINE
    ;
small_stmt
    : expr_stmt
    | pass_stmt
    | flow_stmt
    | import_stmt
    ;

expr_stmt
    : assignable '=' test
    ;

pass_stmt
    : 'pass'
    ;

flow_stmt
    : return_stmt
    ;

return_stmt
    : 'return' test
    ;

import_stmt
    : 'import' IDENTIFIER
    ;

assignable
    : declaration
    | test
    ;

declaration
    : IDENTIFIER ':' type
    ;


compound_stmt
    : if_stmt
    | while_stmt
    | for_stmt
    | funcdef
    ;

if_stmt: 'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?;
while_stmt: 'while' test ':' suite;
for_stmt: 'for' IDENTIFIER 'in' IDENTIFIER ':' suite;

test: or_test;
or_test: and_test ('or' and_test)*;
and_test: not_test ('and' not_test)*;
not_test: 'not' not_test | comparison;
comparison: expr (comp_op expr)*;

comp_op: '<'|'>'|'=='|'<='|'>='|'not'|'!=';
expr: term (('+'|'-') term)*;
term: factor (('*'|'/'|'%') factor)*;
factor: ('+'|'-') factor | atom_expr;
atom_expr: atom trailer*;


atom
    : '(' (arguments)? ')'
    | '[' (arguments)? ')'
    | '{' (dictorsetmakers)? '}'
    | IDENTIFIER
    | literal
    ;

arguments: argument (',' argument)*;
argument: test;

dictorsetmakers
    : dictorsetmaker (',' dictorsetmaker)*
    ;

dictorsetmaker
    : test ':' test
    ;

trailer: '(' (arguments)? ')' | '[' argument ']' | '.' IDENTIFIER;

literal
    : INTEGER_LITERAL
    | FLOAT_LITERAL
    | DOUBLE_LITERAL
    | LONG_LITERAL
    | STRING_LITERAL
    | BOOLEAN_LITERAL
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

NEWLINE                 : '\n';

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