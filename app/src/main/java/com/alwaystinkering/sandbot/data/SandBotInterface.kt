package com.alwaystinkering.sandbot.data

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

import com.alwaystinkering.sandbot.utils.Annotations.Str
import com.alwaystinkering.sandbot.utils.Annotations.Json

interface SandBotInterface {

    //    @GET("/getsettings")
    //    Call<ResponseBody> getSettings();

    @GET("/status")
    fun getStatus(): Call<BotStatus>

    @GET("/exec/g28")
    fun home(): Call<CommandResult>

    @GET("/exec/pause")
    fun pause(): Call<CommandResult>

    @GET("/exec/resume")
    fun resume(): Call<CommandResult>

    @GET("/exec/stop")
    fun stop(): Call<CommandResult>

    @GET("/playFile/{fsName}/{fileName}")
    fun playFile(@Path("fsName") fsName: String, @Path("fileName") name: String): Call<CommandResult>

    @GET("/filelist/")
    fun listFiles(): Call<FileListResult>

    @GET("/files/{fsName}/{fileName}")
    fun getParametricFile(@Path("fsName") fsName: String, @Path("fileName") fileName: String): Call<ParametricFile>

    @Str
    @GET("/files/{fsName}/{fileName}")
    fun getThetaRhoFile(@Path("fsName") fsName: String, @Path("fileName") fileName: String): Call<String>


    //@Headers({"Content-Type: application/json"})
    //    @POST("/postsettings")
    //    Call<ResponseBody> postSettings(@Body RequestBody json);

    @POST("/setled")
    fun setLed(@Body json: RequestBody): Call<ResponseBody>

}

annotation class Str
