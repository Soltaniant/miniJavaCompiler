package org.example.listeners;

import lombok.Getter;
import org.example.*;
import org.example.symboltable.SymbolTable;
import org.example.symboltable.elements.Class;
import org.example.symboltable.elements.Method;
import org.example.symboltable.elements.Variable;

public class SymbolTableCreator extends MiniJavaBaseListener {

    @Getter
    private SymbolTable symbolTable;
    private Class currentClass;
    private Method currentMethod;

    public static void printError(String error) {
        System.err.println();
        System.err.println(error);
        System.exit(-1);
    }

    public static String getVariableType(MiniJavaParser.TypeContext ctx) {
        if (ctx.getText().startsWith("int["))
            return "int[]";

        if (ctx.getText().startsWith("int"))
            return "int";

        return ctx.Identifier().getText();
    }

    @Override
    public void enterProgram(MiniJavaParser.ProgramContext ctx) {
        symbolTable = new SymbolTable();
    }

    /**
     * Adds the main class to the symbol table and sets the current class to
     * the main class
     */
    @Override
    public void enterMainClass(MiniJavaParser.MainClassContext ctx) {
        String id = ctx.Identifier(0).toString();
        Class mainClass = new Class(id);
        currentClass = mainClass;
        symbolTable.getProgram().addClass(id, mainClass);
    }

    @Override
    public void exitMainClass(MiniJavaParser.MainClassContext ctx) {
        currentClass = null;
    }

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        String id = ctx.Identifier(0).toString();

        // Checks the class hasn't already been declared
        if (symbolTable.findClass(id) != null)
            printError("ERROR: Class name \"" + id + "\" already exists");

        Class newClass = new Class(id);

        if (ctx.getText().contains("extend")) {
            newClass.setParentClassId(ctx.Identifier(1).toString());
        }
        currentClass = newClass;
        symbolTable.getProgram().addClass(id, newClass);
    }

    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentClass = null;
    }

    @Override
    public void enterVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        String id = ctx.Identifier().getText();
        String type = getVariableType(ctx.type());
        Variable newVar = new Variable(id, type);
        // Checks if the current scope is a method
        if (currentMethod != null) {
            // Checks if the current method already has a parameter or local variable of the same variable
            if (currentMethod.getLocalVars().get(id) == null && currentMethod.getParameters().get(id) == null) {
                currentMethod.addLocalVariable(id, newVar);
            } else {
                printError("ERROR: Variable \"" + id + "\" already declared within method \"" + currentMethod.getId() + "\"");
            }
        } else {
            // Checks if current closs has a global variable of the same variable
            if (currentClass.getGlobalVars().get(id) == null) {
                currentClass.getGlobalVars().put(id, newVar);
            } else {
                printError("ERROR: Global variable \"" + id + "\" already declared within class \"" + currentClass.getId() + "\"");
            }
        }
    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        String id = ctx.Identifier().getText();
        String type = getVariableType(ctx.type());
        // Checks if method already exists with the class
        if (currentClass.getMethods().get(id) != null) {
            printError("ERROR: Method \"" + id + "\" already defined within class \"" + currentClass.getId() + "\"");
        }
        currentMethod = new Method(id, type);
        currentClass.getMethods().put(id, currentMethod);
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentMethod = null;
    }

    @Override
    public void enterParameterList(MiniJavaParser.ParameterListContext ctx) {
        ctx.parameter().forEach(p -> {
            String id = p.Identifier().getText();
            String type = getVariableType(p.type());

            if (currentMethod.getParameters().get(id) != null)
                printError("Parameter \"" + id + "\" already exists in method \"" + currentMethod.getId() + "\"");

            Variable newVar = new Variable(id, type);
            currentMethod.addParameter(id, newVar);
        });
    }
}


