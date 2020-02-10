package com.alwaystinkering.sandbot.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
