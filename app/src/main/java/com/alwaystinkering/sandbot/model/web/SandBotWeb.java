package com.alwaystinkering.sandbot.model.web;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SandBotWeb {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.170")
            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                    .setLenient()
                    .create()))
            .build();

    private static SandBotInterface sandBotInterface = retrofit.create(SandBotInterface.class);

    public static SandBotInterface getInterface() {
        return sandBotInterface;
    }

    public static void createInterface(String ip) {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .build();
        sandBotInterface = retrofit.create(SandBotInterface.class);
    }

}
