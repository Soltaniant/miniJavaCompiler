# Step1

Initially based on the provided rules of MiniJava langugae which is a simplified version of originial java, we will create the grammar using ANTLR.

```txt
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
```

This code is created based on the information provided by below links. Though it has been somehow refactored so to be more readable and cleaner:

* https://www.cs.tufts.edu/~sguyer/classes/comp181-2006/minijava.html
* https://cs.rit.edu/~hh/teaching/cc17/eminijava
* https://github.com/nsengupta5/MiniJava-TypeChecker

Also as the resources described, below keywords are meaningfull as described below:
* <IDENTIFIER> represents a sequence of letters, digits and underscores, starting with a letter. An identifier is not a keyword. Identifiers are case-sensitive.
* <INTEGER_LITERAL> represents a sequence of digits
* <STRING_LITERAL> represents a sequence of arbitrary characters, except new lines and ". You don't need to support escape characters such as \n.
* <EOF> represents the special end-of-file character

**Note**: we have also attached the LINE and MULTILINE comments as a grammar rule to be skipped!

Similar to what we have in Java itself, here we will behave similarly about the assiciativities and precedences. in case of any ambiguity, below structures will be sufficient to resolve the issue:

```
// associativity of operators
expression ::=  expression ‘*’ expression {left}
expression ::=  expression ‘+’ expressionexpression ::=  expression ‘+’ expression {left}
expression ::=  expression ‘-’ expressionexpression ::=  expression ‘-’ expression {left}
expression ::=  expression ‘<’ expressionexpression ::=  expression ‘<’ expression {non-assoc}
expression ::=  expression ‘&&’ expressionexpression ::=  expression ‘&&’ expression {left}
```

and for the production rules we have:
```
// top priority
expression ::=  expression ‘.’ identifier ‘(’ expression-list? ‘)’expression ::=  expression ‘.’ identifier ‘(’ expression-list? ‘)’
expression ::=  expression ‘[’ expression ‘]’expression ::=  expression ‘[’ expression ‘]’

// second
expression ::=  ‘!’ expression

//more ...
expression ::=  expression ‘*’ expression
expression ::=  expression ‘+’ expression
expression ::=  expression ‘<’ expression
expression ::=  expression ‘&&’ expression
```


More notes on semantics:
* Comments are supported as described in the grammer, though nested comments are not allowed.
* The else branch of the if construct is NOT optional.
* Inheritance works as in Java. eMiniJava doesn't support the interfaces, abstract members or classes.
* eMiniJava does not allow two fields with the same name in a class, or two classes that inherit each other (field overriding is not allowed).
* eMiniJava does not allow two variables with the same name in a method (including parameters and locally defined variables). However, it allows to define a variable in a method whose enclosing class has a field of the same name. In that case, variable shadowing happens: the method variable takes precedence.
* Accessing a field that has not been initialized results in undefined behavior (you can use a default value in this case, but your compiler must not crash in any case).
* The operator precedence is the same as in Java. From highest priority to lowest: !, then * and /, then + and -, then < and ==, then &&, then ||. Also, new binds tighter than . (method call or .length), which binds tighter than [] (array read), which binds tighter than any operator. So 1 + new Foo().bar().baz()[42] means 1 + ( ( ( (new Foo()).bar()).baz())[42]).
* All binary operators are left-associative. E.g. 1-2+3 means (1-2)+3 and not 1-(2+3). (Of course this does not matter for operators of different precedence).


# Step2
now based on the created grammar (`MiniJava.g4`) we will run `antlr4` command to generate the parser and the lexer.
as output, `MiniJavaLexer` and `MiniJavaParser` beside other related files are generated.

```java
ANTLRInputStream input = new ANTLRInputStream(code);
MiniJavaLexer lexer = new MiniJavaLexer(input);
CommonTokenStream tokens = new CommonTokenStream(lexer);
MiniJavaParser parser = new MiniJavaParser(tokens);
ParseTree tree = parser.goal();
```
Also to have a better look into how the statements are working, we have added a temporary walker, with a custom visiter which prints every rule it enters.
```java
CustomListener listener = new CustomListener();
ParseTreeWalker.DEFAULT.walk(listener, tree);
```
> above class has been removed!

# Step3
Before adding anything to a symbol table, we must define what will be added and their properties. so we initially defined some `Element`s
with appropriate fields as below:
* Variable
* Method
* Program
* Class
* Scope

> For more info check each class.

Next we create a `symbolTableCreator` class as a listener in which all identifiers, classes and methods will be initialized to be used by following phases.

# Step 4
Support for inheritance in MiniJava was a bit ambigous and I don't know whether it is supported or not. But we have added a single inheritance mode as in java using the `extend` keyword.
A listener called `InheritanceListerner` is now added to provide such a functionality.

# Step 5
Scope checking was another requirements which is supported using an `ScopeCheckingListener`. based on entering and exiting to a new scope, it will check for existence of variables and identifiers used by program.

# Step 6
Finally for the type checking we have added a `TypeChecker` listener. unfortunately it works a bit falsy in some cases and it is not complete. though in general, it uses an stack approach to pop and push the last defined type, so to check as it needs.
