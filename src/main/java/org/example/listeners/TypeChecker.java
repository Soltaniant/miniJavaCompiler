package org.example.listeners;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.MiniJavaBaseListener;
import org.example.MiniJavaParser;
import org.example.symboltable.SymbolTable;
import org.example.symboltable.Type;
import org.example.symboltable.records.*;
import org.example.symboltable.records.Class;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Predicate;

public class TypeChecker extends MiniJavaBaseListener {

    private final SymbolTable symbolTable;
    private final Stack<Type> typeChecker;

    private Class currentClass;
    private Method currentMethod;

    public TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        typeChecker = new Stack<>();
    }

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentClass = symbolTable.findClass(ctx.Identifier(0).getText());
    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentMethod = currentClass.getMethods().get(ctx.Identifier().getText());
    }

    @Override
    public void exitVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        Variable variable;

        if (currentMethod != null && currentMethod.getLocalVars().get(ctx.Identifier().getText()) != null) {
            variable = currentMethod.getLocalVars().get(ctx.Identifier().getText());
        }
        else if (currentMethod != null && currentMethod.getParameters().get(ctx.Identifier().getText()) != null) {
            variable = currentMethod.getParameters().get(ctx.Identifier().getText());
        }
        else {
            variable = currentClass.getGlobalVars().get(ctx.Identifier().toString());
        }

        if (symbolTable.findClass(variable.getType()) != null)
            typeChecker.push(new Type(symbolTable.findClass(variable.getType())));
        else
            typeChecker.push(new Type(variable.getType()));
    }

    @Override
    public void exitIntegerExpression(MiniJavaParser.IntegerExpressionContext ctx) {
        typeChecker.push(new Type("int"));
    }

    @Override
    public void exitBooleanExpression(MiniJavaParser.BooleanExpressionContext ctx) {
        typeChecker.push(new Type("boolean"));
    }

    @Override
    public void exitThisExpression(MiniJavaParser.ThisExpressionContext ctx) {
        typeChecker.push(new Type(currentClass));
    }

    /**
     * Checks that the new object declaration has a corresponding class declaration
     */
    @Override
    public void exitObjectInstantiationExpression(MiniJavaParser.ObjectInstantiationExpressionContext ctx) {
        String objName = ctx.Identifier().getText();
        Class obj = symbolTable.findClass(objName);
        if (obj == null) {
            // Push even if class doesn't exist
            typeChecker.push(new Type("class"));
            System.err.println("ERROR: Object has no class");
        }
        else {
            typeChecker.push(new Type(obj));
        }
    }

    /**
     * Checks if the return type matches the method's type
     */
    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        Type s1 = typeChecker.pop();
        String t;
        if (s1.getObject() != null) {
            Class c = s1.getObject();
            t = c.getId();
        }
        else {
            t = s1.getElementType();
        }
        if (!currentMethod.getType().equals(t)) {
            System.err.println("ERROR: Invalid return type \"" + t + "\" for method \"" + currentMethod.getId() + "\"");
        }
    }

    @Override
    public void exitIfStatement(MiniJavaParser.IfStatementContext ctx) {
        String type = typeChecker.pop().getElementType();
        if (!type.equals("boolean")) {
            System.err.println("ERROR: Conditions of if statements must be of type boolean");
        }
    }

    @Override
    public void exitWhileStatement(MiniJavaParser.WhileStatementContext ctx) {
        String type = typeChecker.pop().getElementType();
        if (!type.equals("boolean")) {
            System.err.println("ERROR: Conditions of while statements must be of type boolean");
        }
    }

    /**
     * Checks if the "Println" statement correctly contains an int
     */
    @Override
    public void exitPrintExpression(MiniJavaParser.PrintExpressionContext ctx) {
        String type = typeChecker.pop().getElementType();
        if (!type.equals("int")) {
            System.err.println("ERROR: Arguments of Println must be of type integer");
        }
    }

    /**
     * Checks for the correct matching types when assigning variables
     */
    @Override
    public void exitAssignmentStatement(MiniJavaParser.AssignmentStatementContext ctx) {
        Variable variable;
        // Finds the variable in the scope
        if (currentMethod.getLocalVars().get(ctx.Identifier().getText()) != null) {
            variable = currentMethod.getLocalVars().get(ctx.Identifier().getText());
        }
        else if (currentMethod.getParameters().get(ctx.Identifier().getText()) != null) {
            variable = currentMethod.getParameters().get(ctx.Identifier().getText());
        }
        else {
            variable = currentClass.getGlobalVars().get(ctx.Identifier().getText());
        }

        String lhs;
        // Check for objects
        if (symbolTable.findClass(variable.getType()) != null) {
            lhs = "class";
        }
        else {
            lhs = variable.getType();
        }
        String rhs = typeChecker.pop().getElementType();
        if (!lhs.equals(rhs)) {
            System.err.println("ERROR: Incompatible type assignment for variable \"" + variable.getId()  + "\"");
        }
    }
    
    /**
     * Checks if an int[] array is correctly assigned to the right type
     */
    @Override
    public void exitArrayAssignmentStatement(MiniJavaParser.ArrayAssignmentStatementContext ctx) {
        Variable array;

        // Gets the array type
        if (currentMethod.getParameters().get(ctx.Identifier().getText()) != null) {
            array = currentMethod.getParameters().get(ctx.Identifier().getText());
        }
        else if (currentMethod.getLocalVars().get(ctx.Identifier().getText()) != null) {
            array = currentMethod.getLocalVars().get(ctx.Identifier().getText());
        }
        else {
            array = currentClass.getGlobalVars().get(ctx.Identifier().getText());
        }
        String lhs = typeChecker.pop().getElementType();
        String index = typeChecker.pop().getElementType();

        // Checks if the assigned variable is an int[]
        if (!array.getType().equals("int[]")) {
            System.err.println("ERROR: Variable \"" + array.getId() + "\" must be of type int[]");
        }

        // Checks if the type being assigned to int[] index is an integer
        if (!lhs.equals("int")) {
            System.err.println("ERROR: Int array element must be assigned to an integer");
        }

        // Checks that the int[] index is an integer
        if (!index.equals("int")) {
            System.err.println("ERROR: Index of arrays must be an integer");
        }
    }

    /**
     * Adds a marker to denote the start of an expression call
     */
    @Override
    public void enterMethodCallExpression(MiniJavaParser.MethodCallExpressionContext ctx) {
        typeChecker.push(null);
    }

    @Override
    public void exitMethodCallExpression(MiniJavaParser.MethodCallExpressionContext ctx) {
        Stack<Type> args = new Stack<>();

        while (typeChecker.peek() != null) {
            args.push(typeChecker.pop());
        }

        typeChecker.pop();

        Type methodType = args.pop();
        // Checks that the variable calling the method is an object
        if (!methodType.getElementType().equals("class")) {
            System.err.println("ERROR: Only objects can call methods");
            // URGENT: NEED TO LOOK AT THIS
            typeChecker.push(new Type("class"));
            return;
        }

        Class c = methodType.getObject();
        // Checks if class exists
        if (c == null) {
            System.err.println("ERROR: Class does not exist");
            System.exit(-1);
        }
        else {
            // Checks if the object contains the method
            Method m = c.getMethods().get(ctx.Identifier().getText());
            if (m == null) {
                System.err.println("ERROR: Given class \"" + c.getId() + "\" does not contain method");
//                typeChecker.push(new Type("class"));
                return;
            }
            List<String> params = m.getParamTypes();
            // Checks if the method call has the correct number of arguements
            if (params.size() != args.size()) {
                System.err.println("ERROR: Method call has invalid number of arguments");
//                typeChecker.push(new Type("class"));
                return;
            }

            /*Checks that the types in the method call match the order in which is declared
             in the method */
            for (String param : params) {
                Type p = args.pop();
                String t = p.getElementType();
                // CHECK CLASS TYPES E.G ACCEPT METHOD IN TREEVISITOR
                if (p.getObject() != null) {
                    if (!p.getObject().getId().equals(param)) {
                        if (!p.getObject().getParentClassId().equals(param)) {
                            System.err.println("ERROR: Argument type \"" + p.getObject().getId() + "\" does not match parameter type \"" + param + "\"");
                        }
                    }
                }
                else {
                    if (!t.equals(param)) {
                        System.err.println("ERROR: Argument type \"" + t + "\" does not match parameter type\"" + param + "\"");
                    }
                }
            }

            // Pushes the corresponding type to the type checker stack
            if (symbolTable.findClass(m.getType()) != null) {
                typeChecker.push(new Type(symbolTable.findClass(m.getType())));
            }
            else {
                typeChecker.push(new Type(m.getType()));
            }
        }
    }

    @Override
    public void exitOperationExpression(MiniJavaParser.OperationExpressionContext ctx) {
        String recent1 = typeChecker.pop().getElementType();
        String recent2 = typeChecker.pop().getElementType();
        if (ctx.Relation().getText().matches("/+|-|/*|//")) {
            // Checks if integer operations are performed on integers
            if (!(recent1.equals("int") && recent2.equals("int"))) {
                System.err.println("ERROR: Integer operations only allow integers");
            }
        }
        else {
            // Checks if boolean operations are performed on booleans
            if (!(recent1.equals("boolean") && recent2.equals("boolean"))) {
                System.err.println("ERROR: Boolean operations only allow booleans");
            }
        }

        // Pushes boolean onto stack for expressions evaluating to booleans
        if (ctx.Relation().getText().matches("[><]") || ctx.Relation().getText().matches("/+|-|/*|//")) {
            typeChecker.push(new Type("boolean"));
        }
        // Pushes int onto stack for expressions evaluating to int
        else {
            typeChecker.push(new Type("int"));
        }
    }

    @Override
    public void exitArrayIndexExpression(MiniJavaParser.ArrayIndexExpressionContext ctx) {
        String index = typeChecker.pop().getElementType();
        String arr = typeChecker.pop().getElementType();

        // Checks if index is an integer
        if (!index.equals("int")) {
            System.err.println("ERROR: Index has to be an integer");
        }

        // Checks that lookup is being applied to int[]
        if (!arr.equals("int[]")) {
            System.err.println("ERROR: Array lookup has to be an integer");
        }

        typeChecker.push(new Type("int"));
    }

    @Override
    public void exitLengthExpression(MiniJavaParser.LengthExpressionContext ctx) {
        String type = typeChecker.pop().getElementType();
        // Checks that length method only applied to int[]
        if (!type.equals("int[]")) {
            System.err.println("ERROR: Length can only be applied to type int[]");
        }
        typeChecker.push(new Type("int"));
    }

    /**
     * Checks for correct int[] declaration
     */
    @Override
    public void exitArrayDeclarationExpression(MiniJavaParser.ArrayDeclarationExpressionContext ctx) {
        String type = typeChecker.pop().getElementType();
        // Checks size given is an integer
        if (!type.equals("int")) {
            System.err.println("ERROR: Array sizes must be an integer");
        }
        typeChecker.push(new Type("int[]"));
    }

    /**
     * Checks if the not expression contains a boolean
     */
    @Override
    public void exitNotExpression(MiniJavaParser.NotExpressionContext ctx) {
        String s1 = typeChecker.pop().getElementType();
        if (!s1.equals("boolean"))
            System.err.println("ERROR: Not arguments can only be of type boolean");

        typeChecker.push(new Type("boolean"));
    }
}