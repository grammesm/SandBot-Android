package com.alwaystinkering.sandbot.model.pattern;

import androidx.annotation.Nullable;

import com.alwaystinkering.sandbot.data.SandBotFile;

import java.io.Serializable;

public abstract class AbstractPattern implements Serializable {

    public static final String PATTERN_NAME_EXTRA_KEY = "patternName";

    protected String name;
    protected FileType fileType = FileType.PARAM;
    protected int size = 0;
    protected boolean loaded = false;

    public abstract Coordinate processNextEvaluation(int tableDiameter);
    public abstract boolean isStopped();
    public abstract void reset();
    public abstract boolean processSandbotFile(SandBotFile file);
    public abstract boolean validate(int tableDiameter);
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

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof AbstractPattern && this.name.equals(((AbstractPattern) obj).name);
    }
}
