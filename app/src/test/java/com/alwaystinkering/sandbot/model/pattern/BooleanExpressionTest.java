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
    public void testSimpleEqualsEvaluate1() {
        formula = "t > 100";
        vars.put("t", 100.2D);
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

    @Test
    public void testMultVars() {
        formula = "(t>20)";
        vars.put("p", 80.0);
        vars.put("r", 80.0);
        vars.put("t", 26.460000000001337);
        vars.put("w", 3.0);
        vars.put("x", 95.75688291834061);
        vars.put("y", 98.12805182864165);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));
    }

    @Test
    public void testMultVars2() {
        formula = "(t==20)";
        vars.put("p", 80.0);
        vars.put("r", 80.0);
        vars.put("t", 20.0);
        vars.put("w", 3.0);
        vars.put("x", 95.75688291834061);
        vars.put("y", 98.12805182864165);
        expression = new BooleanExpression(formula);
        assertTrue(expression.evaluate(vars));
    }




}