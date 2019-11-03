package com.alwaystinkering.sandbot.model.pattern;

import android.util.Log;

import com.alwaystinkering.sandbot.model.web.SandBotFile;
import com.alwaystinkering.sandbot.ui.sandbot.MainActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParametricPattern extends AbstractPattern{
    private static final String TAG = "ParametricPattern";

    private String declarationString;
    private String expressionString;

    private Map<String, Double> paramToValue = new HashMap<>();
    private List<ExpressionContainer> expToEval = new ArrayList<>();
    private BooleanExpression stopExpression;

    private Map<String, Double> runningParamToValue = new HashMap<>();
    private String validationResults = "";

    public ParametricPattern(String name) {
        super(name, FileType.PARAM);
    }

    public ParametricPattern(String name, String declarations, String expressions) {
        super(name, FileType.PARAM);
        this.name = name;
        this.declarationString = declarations;
        this.expressionString = expressions;
    }

    public void setDeclarationString(String declarationString) {
        this.declarationString = declarationString;
    }

    public void setExpressionString(String expressionString) {
        this.expressionString = expressionString;
    }

    public String getDeclarationString() {
        return declarationString;
    }

    public String getExpressionString() {
        return expressionString;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();

        json.append("\"").append(name).append("\":{")
                .append("\"setup\":\"").append(declarationString.replace("\n", "\\n")).append("\",")
                .append("\"loop\":\"").append(expressionString.replace("\n", "\\n")).append("\"")
                .append("}");
        return json.toString();
    }

    public void reset() {
        runningParamToValue.clear();
        runningParamToValue.putAll(paramToValue);
    }

    @Override
    public boolean processSandbotFile(SandBotFile file) {
        return false;
    }

    @Override
    public Coordinate processNextEvaluation() {
        for (ExpressionContainer exp : expToEval) {
            //Log.d(TAG, "Exp validation: " + exp.getExpression().validate(false).isValid() + ":" + exp.getExpression().validate(false).getErrors());
            runningParamToValue.put(exp.getVar(), exp.getExpression().setVariables(runningParamToValue).evaluate());
        }
        //Log.d(TAG, "result: " + runningParamToValue.toString());
        return new Coordinate(runningParamToValue.get("x").floatValue(), runningParamToValue.get("y").floatValue());
    }

    @Override
    public boolean isStopped() {
        Log.d(TAG, "Evaluating: " + stopExpression.toString());
        Log.d(TAG, "Values: " + runningParamToValue.toString());
        return stopExpression.evaluate(runningParamToValue);
    }

    public boolean validate() {
        paramToValue.clear();
        runningParamToValue.clear();
        boolean valid = true;
        StringBuilder results = new StringBuilder();

        if (expressionString.trim().isEmpty()) {
            valid = false;
            results.append("Empty Expression String");
        } else {
            // Split up input into lines
            String[] declarations = declarationString.split("\n");
            String[] expressions = expressionString.split("\n");

            // Check the declarations
            for (String dec : declarations) {

                results.append("Processing [").append(dec).append("]\n");

                // Operate everything in lower case
                dec = dec.trim().toLowerCase();

                // Make sure there is a declaration in the string
                if (!dec.isEmpty()) {

                    // Replace any variables
                    dec = dec.replace("sizex", Integer.toString(MainActivity.tableDiameter));

                    // Should be 2 parts with an assignment
                    String[] parts = dec.split("=");

                    String var;
                    double value;
                    if (parts.length == 2) {
                        var = parts[0].trim();
                        value = Double.parseDouble(parts[1].trim());
                    } else {
                        results.append("Invalid Declaration: ").append(dec).append("\n");
                        valid = false;
                        continue;
                    }
                    results.append("Pass: [").append(var).append("] init to value [").append(String.format(Locale.US, "%.04f", value)).append("]\n");
                    paramToValue.put(var, value);
                } else {
                    Log.d(TAG, "Ignoring empty declaration");
                }
            }

            // Only process expressions if declarations pass
            if (valid) {

                boolean foundStop = false;
                String stopBool = null;

                for (String exp : expressions) {
                    exp = exp.trim().toLowerCase();

                    // Make sure there's an expression on the line
                    if (!exp.isEmpty()) {

                        // If this is the stop expression
                        if (exp.startsWith("stop")) {
                            String parts[] = exp.split("=");
                            if (parts.length == 2) {
                                stopBool = parts[1].trim();
                                foundStop = true;
                                // tODO
                                //Expression expression = new ExpressionBuilder(stopBool).build();
                                stopExpression = new BooleanExpression(stopBool);
                            } else {
                                Log.e(TAG, "Invalid stop expression: " + exp);
                                valid = false;
                                break;
                            }
                        } else {
                            results.append("Validating [").append(exp).append("]\n");

                            // Ensure there is only one assignment
                            String[] parts = exp.split("=");
                            if (parts.length == 2) {
                                try {
                                    Expression e = new ExpressionBuilder(parts[1].trim())
                                            .variables(paramToValue.keySet())
                                            .build();
                                    ValidationResult r = e.validate(false);
                                    if (!r.isValid()) {
                                        results.append(r.getErrors());
                                        valid = false;
                                        break;
                                    }
//                                    Set<String> variables = e.getVariableNames();
//                                    Log.d(TAG, "Variables found: " + variables);
                                    String var = parts[0].trim();
                                    expToEval.add(new ExpressionContainer(exp, var, e));
                                    if (!paramToValue.containsKey(var)) {
                                        paramToValue.put(var, 0D);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                results.append("Invalid Expression: ").append(exp).append("\n");
                                valid = false;
                                break;
                            }
                        }
                    }
                }

                if (!foundStop) {
                    results.append("No stop expression found");
                    valid = false;
                } else {
                    results.append("Stop Expression: ").append(stopBool);
                }

            }
        }

        // Make sure there'a an x and y assignment
        if (!paramToValue.containsKey("x") || !paramToValue.containsKey("y")) {
            valid = false;
            results.append("No assignment to both x and y found");
        }

        validationResults = results.toString();
        runningParamToValue.putAll(paramToValue);
        Log.d(TAG, "Results: \n" + validationResults);
        return valid;
    }

    public String getValidationResults() {
        return validationResults;
    }
}
