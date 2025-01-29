package org.example;

import org.antlr.v4.runtime.ParserRuleContext;

public class CustomListener extends MiniJavaBaseListener {

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
        System.out.println("Entering Rule:" + ctx.getText());
    }
    }