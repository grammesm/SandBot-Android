package com.alwaystinkering.sandbot.model.pattern;

import junit.framework.TestCase;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ComparisonTest extends TestCase {

    Comparison comparison;
    Map<String, Double> vars = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        vars.clear();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimpleEqualsEvaluate() {
        Expression left = new ExpressionBuilder("x").variable("x").build();
        Expression right = new ExpressionBuilder("0").build();
        vars.put("x", 0D);
        comparison = new Comparison(left, Comparison.Operator.EQUAL_TO, right);
        assertTrue(comparison.evaluate(vars));
    }

    @Test
    public void testSimpleNotEqualsEvaluate() {
        Expression left = new ExpressionBuilder("x").variable("x").build();
        Expression right = new ExpressionBuilder("0").build();
        vars.put("x", 23D);
        comparison = new Comparison(left, Comparison.Operator.NOT_EQUAL_TO, right);
        assertTrue(comparison.evaluate(vars));
    }

    @Test
    public void testSimpleGreaterEvaluate() {
        Expression left = new ExpressionBuilder("x").variable("x").build();
        Expression right = new ExpressionBuilder("45").build();
        vars.put("x", 55D);
        comparison = new Comparison(left, Comparison.Operator.GREATER_THAN, right);
        assertTrue(comparison.evaluate(vars));
    }

    @Test
    public void testSimpleLessEvaluate() {
        Expression left = new ExpressionBuilder("x").variable("x").build();
        Expression right = new ExpressionBuilder("75").build();
        vars.put("x", 55D);
        comparison = new Comparison(left, Comparison.Operator.LESS_THAN, right);
        assertTrue(comparison.evaluate(vars));
    }

    @Test
    public void testMediumEqualsEvaluate() {
        Expression left = new ExpressionBuilder("x + y")
                .variable("x")
                .variable("y")
                .build();
        Expression right = new ExpressionBuilder("var1 - var2")
                .variable("var1")
                .variable("var2")
                .build();
        vars.put("x", 4D);
        vars.put("y", 6.7D);
        vars.put("var1", 15D);
        vars.put("var2", 4.3D);
        comparison = new Comparison(left, Comparison.Operator.EQUAL_TO, right);
        assertTrue(comparison.evaluate(vars));
    }

    @Test
    public void testComplexEqualsEvaluate() {
        Expression left = new ExpressionBuilder("(x + y)*z")
                .variable("x")
                .variable("y")
                .variable("z")
                .build();
        Expression right = new ExpressionBuilder("((var1 - var2) * var3) / var4")
                .variable("var1")
                .variable("var2")
                .variable("var3")
                .variable("var4")
                .build();
        vars.put("x", 4D);
        vars.put("y", 6.7D);
        vars.put("z", 4D);
        vars.put("var1", 15D);
        vars.put("var2", 4.3D);
        vars.put("var3", 4D);
        vars.put("var4", 1D);
        comparison = new Comparison(left, Comparison.Operator.EQUAL_TO, right);
        assertTrue(comparison.evaluate(vars));
    }

}