package com.alwaystinkering.sandbot.model.pattern;

import com.alwaystinkering.sandbot.model.web.SandBotFile;

public abstract class AbstractPattern {

    public static final String PATTERN_NAME_EXTRA_KEY = "patternName";

    protected String name;
    protected FileType fileType = FileType.UNKNOWN;
    protected int size = 0;
    protected boolean loaded = false;

    public abstract Coordinate processNextEvaluation();
    public abstract boolean isStopped();
    public abstract void reset();
    public abstract boolean processSandbotFile(SandBotFile file);
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    protected AbstractPattern(String name, FileType fileType) {
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
}
