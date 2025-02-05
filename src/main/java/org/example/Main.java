package org.example;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.example.listeners.InheritanceListener;
import org.example.listeners.ScopeCheckingStatements;
import org.example.listeners.SymbolTableCreator;
import org.example.listeners.TypeChecker;
import org.example.utils.FileReaderUtil;

import java.io.*;

@SuppressWarnings("deprecation")
public class Main {
    public static void main(String[] args) throws IOException {
        String code = FileReaderUtil.readFileAsString("/home/sajad/IdeaProjects/MiniJavaCompiler/src/main/resources/code.txt");

        ANTLRInputStream input = new ANTLRInputStream(code);
        MiniJavaLexer lexer = new MiniJavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniJavaParser parser = new MiniJavaParser(tokens);
        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();

        SymbolTableCreator sbBuilder = new SymbolTableCreator(parser);
        walker.walk(sbBuilder, tree);

        InheritanceListener inheritance = new InheritanceListener(sbBuilder.getSymbolTable());
        walker.walk(inheritance, tree);

        ScopeCheckingStatements verifier = new ScopeCheckingStatements(inheritance.getSymbolTable());

        walker.walk(verifier, tree);
        verifier.getSymbolTable().printSymbolTable();

//        TypeChecker typecheck = new TypeChecker(verifier.getSymbolTable());
//        walker.walk(typecheck, tree);
    }
}