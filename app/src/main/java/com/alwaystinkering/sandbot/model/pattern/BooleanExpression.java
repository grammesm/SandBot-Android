package com.alwaystinkering.sandbot.model.pattern;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Map;

public class BooleanExpression {

    private static final String TAG = "BooleanExpression";

    private String expression;

    public BooleanExpression(String expression) {
        this.expression = expression;
    }

    public boolean evaluate(Map<String, Double> vars) {

        boolean eval = false;


        String[] op = findOuterComparisons(expression);

        if (op.length == 3) {
            String left = op[0];
            String oper = op[1];
            String right = op[2];
//            Log.d(TAG, "LEFT: " + left + ", Operand: " + oper + ", RIGHT: " + right);

            Comparison.Operator operator = Comparison.Operator.fromString(oper);

            if (operator != Comparison.Operator.UNDEFINED) {
                if (operator == Comparison.Operator.AND || operator == Comparison.Operator.OR) {
//                    Log.d(TAG, "LOGIC");
                    if (operator == Comparison.Operator.AND) {
                        return new BooleanExpression(left).evaluate(vars) && new BooleanExpression(right).evaluate(vars);
                    } else {
                        return new BooleanExpression(left).evaluate(vars) || new BooleanExpression(right).evaluate(vars);
                    }
                } else {
//                    Log.d(TAG, "EVALUATE");
                    Expression leftExpression = new ExpressionBuilder(left).variables(vars.keySet()).build();
                    Expression rightExpression = new ExpressionBuilder(right).variables(vars.keySet()).build();
                    eval = new Comparison(leftExpression, operator, rightExpression).evaluate(vars);
                }
            }
        }
        return eval;
    }

    private String[] findOuterComparisons(String expression) {
        expression = expression.trim();

        int parenCount = 0;

        if (expression.charAt(0) == '(' && expression.charAt(expression.length() - 1) == ')') {
            expression = expression.substring(1, expression.length() - 1);
        }

        char[] expressionArray = expression.toCharArray();


//        Log.d(TAG, "Looking for outer comparison: " + expression);


        for (int pos = 0; pos < expression.length(); pos++) {
            if (expressionArray[pos] == '(') {
                parenCount++;
                continue;
            }
            if (expressionArray[pos] == ')') {
                parenCount--;
                continue;
            }

            // We encountered some sort of operator
            int opLength = isOpType(expressionArray[pos]);
            if (opLength != 0) {

                // No parens yet, must be outer portion
                if (parenCount == 0) {

                    // Boolean operator!
                    if (isBoolOperator(expression.substring(pos, pos + opLength))) {
//                        Log.d(TAG, "Boolean! Left: " + expression.substring(0, pos).trim()
//                                + " Operator: " + expression.substring(pos, pos + 2).trim()
//                                + ", Right: " + expression.substring(pos + 2, expression.length()).trim());

                        return new String[]{
                                expression.substring(0, pos).trim(),
                                expression.substring(pos, pos + 2).trim(),
                                expression.substring(pos + 2, expression.length()).trim()
                        };
                    }
                }
            }
        }

        return new String[]{""};
    }

    private int isOpType(char character) {
        // TODO Support >= and <=
        switch (character) {
            case '<':
            case '>':
                return 1;
            case '=':
            case '!':
            case '&':
            case '|':
                return 2;
            default:
                return 0;
        }
    }

    private boolean isBoolOperator(String s) {
        //Log.d(TAG, "Testing [" + s + "] for boolean operator");
        return Comparison.Operator.fromString(s) != Comparison.Operator.UNDEFINED;
    }
}
