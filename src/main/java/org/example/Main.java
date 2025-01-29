package org.example;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
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
        ParseTree tree = parser.goal();

        CustomListener listener = new CustomListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);
    }
}