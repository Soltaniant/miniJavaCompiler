# Step1

Initially based on the provided rules of MiniJava langugae which is a simplified version of originial java, we will create the grammar using ANTLR.

```txt
grammar MiniJava;

goal                :   mainClass classDeclaration* EOF;

Identifier          :   [a-zA-Z_][0-9a-zA-Z_]*;

mainClass           :   'class' Identifier '{' 'public' 'static' 'void' 'main' '(' 'String' '[' ']' Identifier ')' '{' statement '}' '}';
classDeclaration    :   'class' Identifier ( 'extends' Identifier )? '{' varDeclaration* methodDeclaration* '}';
methodDeclaration   :   'public' type Identifier '(' parameterList? ')' '{' varDeclaration* statement* 'return' expression ';' '}';

type                :    'int'
                    |    'int' '[' ']'
                    |    'boolean'
                    |    Identifier;

statement           :    '{' statement* '}'
                    |    'if' '(' expression ')' statement 'else' statement
                    |    'while' '(' expression ')' statement
                    |    'System.out.println' '(' expression ')' ';'
                    |    Identifier '=' expression ';'
                    |    Identifier '[' expression ']' '=' expression ';';

expression          :    expression '.length'
                    |    expression '[' expression ']'
                    |    expression '.' Identifier '(' ( expression ( ',' expression )* )? ')'
                    |    expression Relation expression
                    |    'this'
                    |    'new' 'int' '[' expression ']'
                    |    'new' Identifier '(' ')'
                    |    '!' expression
                    |    '(' expression ')'
                    |    IntegerLiteral
                    |    Decimal
                    |    Boolean
                    |    Identifier;

parameter           :   type Identifier;
varDeclaration      :   type Identifier ';';
parameterList       :   parameter (',' parameter)*;

Boolean             :   'true' | 'false';
Relation            :   '**' | '*' | '/' | '+' | '-' | '>' | '<' | '=' | '&&' | '||';
IntegerLiteral      :   '0' | [1-9][0-9]*;
Decimal             :   IntegerLiteral? '.' [0-9]*;
WhiteSpace          :   [ \r\t\n]+ -> skip;
MULTILINE_COMMENT   :   '/*' .*? '*/' -> skip;
LINE_COMMENT        :   '//' .*? '\n' -> skip;
```

This code is created based on the information provided by below links. Though it has been somehow refactored so to be more readable and cleaner:

* https://www.cs.tufts.edu/~sguyer/classes/comp181-2006/minijava.html
* https://cs.rit.edu/~hh/teaching/cc17/eminijava

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

In case of Semantics, we will have below notes:

Addition ::
```
7 + 5       => 12
"a" + "b"   => "ab"
"a" + 10    => "a10"
10 + "a"    => "10a"
```

for other opertaions [*, /, -], it only works for int values.

The semantics of equality ::
* value-equality for int, boolean and string
* reference-equality for objects and arrays
```
4 == 4                         => true
new foo() == new foo()         => false
new bar() == new bar()         => false
"string" == 10                 => // Type Error...
"str" + "ing" == "st" + "ring" => true
```

More notes on semantics:
* Comments are supported as described in the grammer, though nested comments are not allowed.
* The else branch of the if construct is optional.
* Inheritance works as in Java. eMiniJava doesn't support the interfaces, abstract members or classes.
* eMiniJava does not allow two fields with the same name in a class, or two classes that inherit each other (field overriding is not allowed).
* eMiniJava does not allow two variables with the same name in a method (including parameters and locally defined variables). However, it allows to define a variable in a method whose enclosing class has a field of the same name. In that case, variable shadowing happens: the method variable takes precedence.
* Accessing a field that has not been initialized results in undefined behavior (you can use a default value in this case, but your compiler must not crash in any case).
* Only constant strings (strings given as literals) are allowed.
* Although the grammar specifies that expressions such as "foobar".method() are legal, they have no meaning in eMiniJava (they would result in a type error). The only operations allowed are: concatenating strings with other strings or with integers, printing strings, passing strings as argument and returning strings.
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

# Step3
Adding symbol table and filling it.