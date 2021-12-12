package com.alwaystinkering.sandbot.util;

import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StringOrJsonConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Annotations.Str.class) {
                return ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit);
            }
            if (annotation.annotationType() == Annotations.Json.class) {
                return GsonConverterFactory.create(new GsonBuilder().setLenient().create()).responseBodyConverter(type, annotations, retrofit);
            }
        }
        return GsonConverterFactory.create(new GsonBuilder().setLenient().create()).responseBodyConverter(type, annotations, retrofit);
    }
}