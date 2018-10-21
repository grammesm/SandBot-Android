package com.alwaystinkering.sandbot.model.web;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SandBotInterface {
    @GET("/exec/g28")
    Call<Result> home();

    @GET("/exec/pause")
    Call<Result> pause();

    @GET("/exec/resume")
    Call<Result> resume();

    @GET("/exec/stop")
    Call<Result> stop();

    @GET("/pattern/{name}")
    Call<Result> startPattern(@Path("name") String name);

    @GET("/getsettings")
    Call<ResponseBody> getSettings();
}
