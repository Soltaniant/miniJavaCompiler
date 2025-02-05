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


statement           :    '{' statement* '}' #BlockStatement
                    |    'if' '(' expression ')' statement 'else' statement #IfStatement
                    |    'while' '(' expression ')' statement #WhileStatement
                    |    'System.out.println' '(' expression ')' ';' #PrintExpression
                    |    Identifier '=' expression ';' #AssignmentStatement
                    |    Identifier '[' expression ']' '=' expression ';' #ArrayAssignmentStatement;

expression          :    expression '.length' #LengthExpression
                    |    expression '[' expression ']' #ArrayIndexExpression
                    |    expression '.' Identifier '(' ( expressionList )? ')' #MethodCallExpression
                    |    expression Relation expression #OperationExpression
                    |    'this' #ThisExpression
                    |    'new' 'int' '[' expression ']' #ArrayDeclarationExpression
                    |    'new' Identifier '(' ')' #ObjectInstantiationExpression
                    |    '!' expression #NotExpression
                    |    '(' expression ')' #GroupExpression
                    |    IntegerLiteral #IntegerExpression
                    |    Decimal #DecimalExpression
                    |    Boolean #BooleanExpression
                    |    Identifier #IdentifierExpression;

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