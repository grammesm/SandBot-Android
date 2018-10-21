package com.alwaystinkering.sandbot.model.pattern;

import net.objecthunter.exp4j.Expression;

import java.util.Map;

public class Comparison {

    public enum Operator {
        UNDEFINED(""),
        LESS_THAN("<"),
        GREATER_THAN(">"),
        NOT_EQUAL_TO("!="),
        AND("&&"),
        OR("||"),
        EQUAL_TO("==");

        Operator(String pattern) {
            this.pattern = pattern;
        }

        private String pattern;

        public String getPattern() {
            return pattern;
        }

        public static Operator fromString(String string) {
            for (Operator operator : Operator.values()) {
                if (operator.getPattern().equals(string)) {
                    return operator;
                }
            }
            return UNDEFINED;
        }
    }

    private Expression left;
    private Operator operator;
    private Expression right;

    public Comparison(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public boolean evaluate(Map<String, Double> vars) {
        double leftValue = left.setVariables(vars).evaluate();
        double rightValue = right.setVariables(vars).evaluate();
        switch (operator) {

            case LESS_THAN:
                return leftValue < rightValue;
            case GREATER_THAN:
                return leftValue > rightValue;
            case NOT_EQUAL_TO:
                return leftValue != rightValue;
            case EQUAL_TO:
                return Math.abs(leftValue - rightValue) < 0.0000001;
            default:
                return false;
        }
    }
}
