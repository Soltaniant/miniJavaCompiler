package org.example.listeners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.MiniJavaBaseListener;
import org.example.MiniJavaParser;
import org.example.symboltable.SymbolTable;
import org.example.symboltable.records.Class;
import org.example.symboltable.records.Method;
import org.example.symboltable.records.Variable;

@RequiredArgsConstructor
public class ScopeCheckingStatements extends MiniJavaBaseListener {

    @Getter
    private final SymbolTable symbolTable;

    private Class currentClass;
    private Method currentMethod;

    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        String id = ctx.Identifier(0).getText();
        currentClass = symbolTable.findClass(id);
    }

    @Override
    public void exitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentClass = null;
    }

    @Override
    public void enterVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        if (ctx.type().Identifier() != null) {
            String objectType = ctx.type().Identifier().getText();
            // Checks if object exists
            if (symbolTable.getProgram().getClasses().get(objectType) == null) {
                SymbolTableCreator.printError("ERROR: Object \"" + objectType + "\" doesn't exist");
            }
        }
    }

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentMethod = currentClass.getMethods().get(ctx.Identifier().getText());
    }

    @Override
    public void exitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentMethod = null;
    }

    @Override
    public void enterParameter(MiniJavaParser.ParameterContext ctx) {
        String id = ctx.Identifier().getText();
        String type = SymbolTableCreator.getVariableType(ctx.type());
        Variable v = currentClass.getGlobalVars().get(id);
        if (v != null) {
            if (v.getType().equals(type)) {
                SymbolTableCreator.printError("ERROR: Cannot use global variable \"" + id + "\" within parameters");
            }
        }
    }

    @Override
    public void enterAssignmentStatement(MiniJavaParser.AssignmentStatementContext ctx) {
        if (ctx.Identifier() != null) {
            String varName = ctx.Identifier().getText();
            if (currentMethod.getLocalVars().get(varName) == null && currentMethod.getParameters().get(varName) == null) {
                if (currentClass.getGlobalVars().get(varName) == null) {
                    SymbolTableCreator.printError("ERROR: Variable \"" + varName + "\" is not in scope");
                }
            }
        }
    }

    @Override
    public void enterArrayAssignmentStatement(MiniJavaParser.ArrayAssignmentStatementContext ctx) {
        if (ctx.Identifier() != null) {
            String varName = ctx.Identifier().getText();
            if (currentMethod.getLocalVars().get(varName) == null && currentMethod.getParameters().get(varName) == null) {
                if (currentClass.getGlobalVars().get(varName) == null) {
                    SymbolTableCreator.printError("ERROR: int[] Variable \"" + varName + "\" is not in scope");
                }
            }
        }
    }

    @Override
    public void exitIdentifierExpression(MiniJavaParser.IdentifierExpressionContext ctx) {
        String varName = ctx.Identifier().getText();
        if (currentMethod.getLocalVars().get(varName) == null && currentMethod.getParameters().get(varName) == null) {
            if (currentClass.getGlobalVars().get(varName) == null) {
                SymbolTableCreator.printError("ERROR: Variable \"" + varName + "\" in expression is not in scope");
            }
        }
    }

    @Override
    public void exitObjectInstantiationExpression(MiniJavaParser.ObjectInstantiationExpressionContext ctx) {
        if (symbolTable.findClass(ctx.Identifier().getText()) == null) {
            SymbolTableCreator.printError("ERROR: Object \"" + ctx.Identifier().getText() + "\" does not have class declaration");
        }
    }
}