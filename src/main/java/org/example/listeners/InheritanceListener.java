package org.example.listeners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.MiniJavaBaseListener;
import org.example.MiniJavaParser;
import org.example.symboltable.SymbolTable;
import org.example.symboltable.elements.Class;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class InheritanceListener extends MiniJavaBaseListener {

    private final SymbolTable symbolTable;

    /**
     * By entering a class declaration we will check for inheritance, so that if it extends any other
     * class, we will add parent classes' methods and global variables to current class.
     * <p>
     * Note: this method will also check for cyclic inheritance.
     */
    @Override
    public void enterClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        String id = ctx.Identifier(0).getText();
        Class currClass = symbolTable.findClass(id);
        if (ctx.getText().contains("extend")) {
            String parentClassId = ctx.Identifier(1).getText();
            List<String> parents = new ArrayList<>();

            while (parentClassId != null) {
                if (parents.contains(parentClassId))
                    SymbolTableCreator.printError("ERROR: Cyclic inheritance is not allowed");

                parents.add(parentClassId);

                Class parent = symbolTable.getProgram().getClasses().get(parentClassId);
                parent.getGlobalVars().forEach((k,v) -> currClass.getGlobalVars().putIfAbsent(k, v));
                parent.getMethods().forEach((k,v) -> currClass.getMethods().putIfAbsent(k, v));
                parentClassId = parent.getParentClassId();
            }
        }
    }
}