package com.alwaystinkering.sandbot.model.web;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET("/sequence/{name}")
    Call<Result> startSequence(@Path("name") String name);

    @GET("/getsettings")
    Call<ResponseBody> getSettings();

    @GET("/status")
    Call<ResponseBody> getStatus();

    @GET("/filelist/")
    Call<FileListResult> listFiles();

    @GET("/files/{fsName}/{fileName}")
    Call<ParametricFile> getParametricFile(@Path("fsName") String fsName, @Path("fileName") String fileName);


    //@Headers({"Content-Type: application/json"})
    @POST("/postsettings")
    Call<ResponseBody> postSettings(@Body RequestBody json);

    @POST("/setled")
    Call<ResponseBody> setLed(@Body RequestBody json);

}
