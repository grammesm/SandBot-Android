package com.alwaystinkering.sandbot.model.pattern;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BooleanExpressionTest extends TestCase {

    Map<String, Double> vars = new HashMap<>();
    BooleanExpression expression;
    String formula;



    @Override
    public void setUp() throws Exception {
        super.setUp();
        vars.clear();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSimpleEqualsEvaluate() {
        formula = "x == 0";
        vars.put("x", 0D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }

    @Test
    public void testSimpleEqualsEvaluate2() {
        formula = "x + y == z - y";
        vars.put("x", 6.2D);
        vars.put("y", 5D);
        vars.put("z", 16.2D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }

    @Test
    public void testSimpleEqualsEvaluateParen2() {
        formula = "(x + y) == (z - y)";
        vars.put("x", 6.2D);
        vars.put("y", 5D);
        vars.put("z", 16.2D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }

    @Test
    public void testSimpleNotEqualsEvaluate() {
        formula = "x != 0";
        vars.put("x", 0D);
        expression = new BooleanExpression(formula);
        assertFalse(expression.evaluate(vars));

    }

    @Test
    public void testSimpleNotEqualsEvaluateParen() {
        formula = "(x + y) != (z - y)";
        vars.put("x", 5.2D);
        vars.put("y", 5D);
        vars.put("z", 16.2D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }

    @Test
    public void testCompoundExpression() {
        formula = "((x == y) && (z == y))";
        vars.put("x", 5D);
        vars.put("y", 5D);
        vars.put("z", 5D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }

    @Test
    public void testCompoundExpression2() {
        formula = "((x == y) || (z == y))";
        vars.put("x", 6D);
        vars.put("y", 5D);
        vars.put("z", 5D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }

    @Test
    public void testCompoundExpression3() {
        formula = "((x < y) || (z > y))";
        vars.put("x", 6D);
        vars.put("y", 5D);
        vars.put("z", 6D);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));

    }




}