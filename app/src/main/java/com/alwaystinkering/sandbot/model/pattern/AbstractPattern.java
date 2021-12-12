package com.alwaystinkering.sandbot.model.pattern;

import androidx.annotation.Nullable;

import java.io.Serializable;

public abstract class AbstractPattern implements Serializable {

    public static final String PATTERN_NAME_EXTRA_KEY = "patternName";

    protected String name;
    protected FileType fileType = FileType.PARAM;
    protected int size = 0;

    public Coordinate processNextEvaluation(int tableDiameter) { return null;}
    public boolean isStopped() { return false;}
    public void reset() {}
    public boolean validate(int tableDiameter) { return false;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public AbstractPattern(String name, FileType fileType) {
        this.name = name;
        this.fileType = fileType;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof AbstractPattern && this.name.equals(((AbstractPattern) obj).name);
    }
}
