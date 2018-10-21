package com.alwaystinkering.sandbot.model.pattern;

import net.objecthunter.exp4j.Expression;

public class ExpressionContainer {

    private String originalString;
    private String var;
    private Expression expression;

    public ExpressionContainer(String originalString, String var, Expression expression) {
        this.originalString = originalString;
        this.var = var;
        this.expression = expression;
    }

    public String getOriginalString() {
        return originalString;
    }

    public String getVar() {
        return var;
    }

    public Expression getExpression() {
        return expression;
    }


}
