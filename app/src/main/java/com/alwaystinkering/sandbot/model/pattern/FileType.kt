package com.alwaystinkering.sandbot.model.pattern

enum class FileType constructor(val extension: String) {
    PARAM("param"),
//    GCODE("gcode"),
    THETA_RHO("thr"),
    SEQUENCE("seq");

    companion object {

        fun fromExtension(extension: String): FileType {
            for (f in values()) {
                if (extension == f.extension) {
                    return f
                }
            }
            return PARAM
        }
    }

}
