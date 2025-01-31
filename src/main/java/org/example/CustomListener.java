package org.example;

import org.antlr.v4.runtime.ParserRuleContext;
import org.example.symboltable.SymbolTable;
import org.example.symboltable.SymbolTableEntry;

public class CustomListener extends MiniJavaBaseListener {

    public SymbolTable symbolTable = new SymbolTable();

    @Override
    public void enterMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        String methodName = ctx.Identifier().getText();
        String returnType = ctx.type().getText(); // Adjust based on grammar
        // Add method entry to the symbol table
        if (symbolTable.exists(methodName)) {
            System.out.println("Error: Method " + methodName + " already declared.");
        } else {
            symbolTable.put(methodName, new SymbolTableEntry(returnType, true));
        }
    }

    @Override
    public void enterVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        String varName = ctx.Identifier().getText();
        String varType = ctx.type().getText();
        // Add entry to the symbol table
        if (symbolTable.exists(varName)) {
            System.out.println("Error: Variable " + varName + " already declared.");
        } else {
            symbolTable.put(varName, new SymbolTableEntry(varType, false));
        }
    }
}