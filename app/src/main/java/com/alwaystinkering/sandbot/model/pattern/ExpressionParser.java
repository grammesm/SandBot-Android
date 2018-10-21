package com.alwaystinkering.sandbot.model.pattern;

import android.util.Log;

public class ExpressionParser {

    private static final String TAG = "ExpressionParser";

    private static final String[] operators = { "!=", "==", ">=", "<=", ">", "<", "||", "&&", "*", "/", "+", "-", "^" };

    public static boolean parseAndEvaluateExpression(String ex)
    {
        for (char c : ex.toCharArray())
        {
            if (!Character.isSpaceChar(c))
                return parseWithStrings(ex);
        }
        Log.e(TAG, "ERROR: Expression cannot be empty!");
        return false;
    }

    @SafeVarargs
    public static <T> boolean evaluate(String or, T... rep)
    {
        String[] temp = new String[rep.length];
        for (int i = 0; i < rep.length; i++)
            temp[i] = "" + rep[i];
        return evaluate(or, temp);
    }

    public static boolean evaluate(String or, String... vars)
    {
        if ((vars.length % 2 == 1 || vars.length < 2) && vars.length != 0)
        {
            Log.e(TAG, "ERROR: Invalid arguments!");
            return false;
        }
        for (int i = 0; i < vars.length; i += 2)
            or = or.replace("[" + vars[i] + "]", "" + vars[i + 1]);
        return parseAndEvaluateExpression(or);
    }

    public static boolean parseWithStrings(String s)
    {
        int[] op = determineOperatorPrecedenceAndLocation(s);
        int start = op[0];
        String left = s.substring(0, start).trim();
        String right = s.substring(op[1]).trim();
        String oper = s.substring(start, op[1]).trim();
        int logType = logicalOperatorType(oper);
        Log.d(TAG, "PARSE: Left: \"" + left + "\" Right: \"" + right + "\" Operator: \"" + oper + "\"");
        if (logType == 0) // encounters OR- recurse
            return parseWithStrings(left) || parseWithStrings(right);
        else if (logType == 1) // encounters AND- recurse
            return parseWithStrings(left) && parseWithStrings(right);
        if (containsMathematicalOperator(left)) // evaluate mathematical expression
            left = "" + parseMathematicalExpression(left);
        if (containsMathematicalOperator(right))// see above
            right = "" + parseMathematicalExpression(right);
        String leftSansParen = removeParens(left);
        String rightSansParen = removeParens(right);
        if (isInt(leftSansParen) && isInt(rightSansParen))
            return evaluate(Double.parseDouble(leftSansParen), oper, Double.parseDouble(rightSansParen));
        else
            return evaluate(leftSansParen, oper, rightSansParen); // assume they are strings
    }

    public static int[] determineOperatorPrecedenceAndLocation(String s)
    {
        s = s.trim();
        int minParens = Integer.MAX_VALUE;
        int[] currentMin = null;
        for (int sampSize = 1; sampSize <= 2; sampSize++)
        {
            for (int locInStr = 0; locInStr < (s.length() + 1) - sampSize; locInStr++)
            {
                int endIndex = locInStr + sampSize;
                String sub;
                if ((endIndex < s.length()) && s.charAt(endIndex) == '=')
                    sub = s.substring(locInStr, ++endIndex).trim();
                else
                    sub = s.substring(locInStr, endIndex).trim();
                if (isOperator(sub))
                {
                    // Idea here is to weight logical operators so that they will still be selected over other operators
                    // when no parens are present
                    int parens = (logicalOperatorType(sub) > -1) ? parens(s, locInStr) - 1 : parens(s, locInStr);
                    if (containsMathematicalOperator(sub))
                    {
                        // Order of operations weighting
                        switch (sub)
                        {
                            case "^":
                            case "/":
                            case "*":
                                parens++;
                                break;
                            case "+":
                            case "-":
                                break;
                        }
                    }
                    if (parens <= minParens)
                    {
                        minParens = parens;
                        currentMin = new int[] { locInStr, endIndex, parens };
                    }
                }
            }
        }
        return currentMin;
    }

    public static int logicalOperatorType(String op)
    {
        switch (op.trim())
        {
            case "||":
                return 0;
            case "&&":
                return 1;
            default:
                return -1;
        }
    }

    public static boolean containsMathematicalOperator(String s)
    {
        s = s.trim();
        for (char c : s.toCharArray())
            if (c == '/' || c == '+' || c == '*' || c == '-' || c == '^')
                return true;
        return false;
    }

    private static int parens(String s, int loc)
    {
        int parens = 0;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == '(' && i < loc)
                parens++;
            if (s.charAt(i) == ')' && i >= loc)
                parens++;
        }
        return parens;
    }

    public static String removeParens(String s)
    {
        s = s.trim();
        String keep = "";
        for (char c : s.toCharArray())
        {
            if (!(c == '(') && !(c == ')'))
                keep += c;
        }
        return keep.trim();
    }

    public static boolean isOperator(String op)
    {
        op = op.trim();
        for (String s : operators)
        {
            if (s.equals(op))
                return true;
        }
        return false;
    }

    public static boolean isInt(String s)
    {
        for (char c : s.toCharArray())
            if (!Character.isDigit(c) && c != '.')
                return false;
        return true;
    }

    public static boolean evaluate(double left, String op, double right)
    {
        switch (op)
        {
            case "==":
                return left == right;
            case ">":
                return left > right;
            case "<":
                return left < right;
            case "<=":
                return left <= right;
            case ">=":
                return left >= right;
            case "!=":
                return left != right;
            default:
                Log.e(TAG, "evaluate() ERROR: Operator type [" + op + "]not recognized.");
                return false;
        }
    }

    public static double parseMathematicalExpression(String s)
    {
        int[] op = determineOperatorPrecedenceAndLocation(s);
        int start = op[0];
        String left = s.substring(0, start).trim();
        String right = s.substring(op[1]).trim();
        String oper = s.substring(start, op[1]).trim();
        Log.d(TAG, "MATH:  Left: \"" + left + "\" Right: \"" + right + "\" Operator: \"" + oper + "\"");
        if (containsMathematicalOperator(left))
            left = "" + parseMathematicalExpression(left);
        if (containsMathematicalOperator(right))
            right = "" + parseMathematicalExpression(right);
        return evaluateSingleMathematicalExpression(Double.parseDouble(removeParens(left)), oper,
                Double.parseDouble(removeParens(right)));
    }

    public static double evaluateSingleMathematicalExpression(double result1, String oper, double result2)
    {
        switch (oper)
        {
            case "*":
                return result1 * result2;
            case "/":
                return result1 / result2;
            case "-":
                return result1 - result2;
            case "+":
                return result1 + result2;
            case "^":
                return Math.pow(result1, result2);
            default:
                Log.e(TAG, "evaluateSingleMathematicalExpression() MATH ERROR: Mismatched Input. OP: " + oper);
                return 0;
        }
    }

    public static boolean evaluate(String left, String op, String right)
    {
        switch (op)
        {
            case "==":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
            default:
                Log.e(TAG, "evaluate() ERROR: Operator type [" + op + "] not recognized.");
                return false;
        }
    }    
}
