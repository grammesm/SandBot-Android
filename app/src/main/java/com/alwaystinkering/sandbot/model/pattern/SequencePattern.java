package com.alwaystinkering.sandbot.model.pattern;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SequencePattern extends AbstractPattern {
    private static final String TAG = "ParametricPattern";

    private List<String> sequenceContents;

    public SequencePattern(String name) {
        super(name, FileType.SEQUENCE);
    }

    public SequencePattern(String name, String contents) {
        super(name, FileType.SEQUENCE);
        this.sequenceContents = Arrays.asList(contents.split(Pattern.quote("\n")));
    }

    public List<String> getSequenceContents() {
        return sequenceContents;
    }

    public void setSequenceContents(List<String> sequenceContents) {
        this.sequenceContents = sequenceContents;
    }

    @Override
    public Coordinate processNextEvaluation(int tableDiameter) {
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
