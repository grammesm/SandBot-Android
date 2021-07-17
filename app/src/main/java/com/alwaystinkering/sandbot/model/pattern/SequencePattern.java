package com.alwaystinkering.sandbot.model.pattern;

import com.alwaystinkering.sandbot.data.SandBotFile;

public class SequencePattern extends AbstractPattern {
    private static final String TAG = "ParametricPattern";

    public SequencePattern(String name) {
        super(name, FileType.SEQUENCE);
    }


    @Override
    public boolean processSandbotFile(SandBotFile file) {
        return false;
    }

    @Override
    public Coordinate processNextEvaluation(int tableDiameter) {
//        for (ExpressionContainer exp : expToEval) {
//            Log.d(TAG, "Exp validation: " + exp.getExpression().validate(false).isValid() + ":" + exp.getExpression().validate(false).getErrors());
//            runningParamToValue.put(exp.getVar(), exp.getExpression().setVariables(runningParamToValue).evaluate());
//        }
//        Log.d(TAG, "result: " + runningParamToValue.toString());
//        return new Coordinate(runningParamToValue.get("x").floatValue(), runningParamToValue.get("y").floatValue());
        return null;
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean validate(int tableDiameter) {
        return true;
    }
}
