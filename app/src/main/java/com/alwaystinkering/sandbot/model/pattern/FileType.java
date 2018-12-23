package com.alwaystinkering.sandbot.model.pattern;

public enum FileType {
    PARAM("param"),
    GCODE("gcode"),
    THETA_RHO("thr"),
    SEQUENCE("seq"),
    UNKNOWN("");

    FileType(String extension) {
        this.extension = extension;
    }

    private String extension;

    public String getExtension() {
        return extension;
    }

    public static FileType fromExtension(String extension) {
        for (FileType f : values()) {
            if (extension.equals(f.getExtension())) {
                return f;
            }
        }
        return UNKNOWN;
    }

}
