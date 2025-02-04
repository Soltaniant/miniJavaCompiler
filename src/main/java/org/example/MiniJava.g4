grammar MiniJava;

program                :   mainClass classDeclaration* EOF;

mainClass           :   'class' Identifier '{' 'public' 'static' 'void' 'main' '(' 'String' '[' ']' Identifier ')' '{' statement '}' '}';

// variables should be declared on top of the class!
classDeclaration    :   'class' Identifier ( 'extends' Identifier )? '{' varDeclaration* methodDeclaration* '}';

varDeclaration      :   type Identifier ';';

// variables should be declared on top of the method.
// all methods should have a return value!
methodDeclaration   :   'public' type Identifier '(' parameterList? ')' '{' varDeclaration* statement* 'return' expression ';' '}';

parameter           :   type Identifier;
parameterList       :   parameter (',' parameter)*;

type                :    'int'
                    |    'int' '[' ']'
                    |    'boolean'
                    |    Identifier;

statement           :    '{' statement* '}' //block
                    |    'if' '(' expression ')' statement 'else' statement
                    |    'while' '(' expression ')' statement
                    |    'System.out.println' '(' expression ')' ';'
                    |    Identifier '=' expression ';'
                    |    Identifier '[' expression ']' '=' expression ';';

expression          :    expression '.length'
                    |    expression '[' expression ']'
                    |    expression '.' Identifier '(' ( expressionList )? ')' //method call
                    |    expression Relation expression
                    |    'this'
                    |    'new' 'int' '[' expression ']' //array declaration
                    |    'new' Identifier '(' ')' //instantiation
                    |    '!' expression
                    |    '(' expression ')'
                    |    IntegerLiteral
                    |    Decimal
                    |    Boolean
                    |    Identifier;

expressionList      :   expression ( ',' expression )*;

Boolean             :   'true' | 'false';
Relation            :   '*' | '/' | '+' | '-' | '>' | '<' | '=' | '&&' | '||';
IntegerLiteral      :   '0' | [1-9][0-9]*;
Decimal             :   IntegerLiteral? '.' [0-9]*;

Identifier          :   Letter (Letter | Digit)*;
Letter              :   [a-zA-Z_];
Digit               :   [0-9];

WhiteSpace          :   [ \r\t\n]+ -> skip;

MultilineComment    :   '/*' .*? '*/' -> skip;
LineComment         :   '//' .*? '\n' -> skip;