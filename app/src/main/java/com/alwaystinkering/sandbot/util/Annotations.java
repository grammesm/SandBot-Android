package com.alwaystinkering.sandbot.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class Annotations {
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface Str {
    }
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface Json {
    }
}
