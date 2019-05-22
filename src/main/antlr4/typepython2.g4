grammar typepython2;

file_input              : (NEWLINE | statement)* EOF;

funcdef                 : DEF NAME parameters ':' suite;
suite: NEWLINE INDENT statement+ DEDENT;

parameters: '(' (typedargslist)? ')';

typedargslist: tfpdef (',' tfpdef)*;
tfpdef: NAME (':' type)?;

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

statement: simple_stmt | compound_stmt;

compound_stmt
    : if_stmt
    | while_stmt
    | for_stmt
    | funcdef

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