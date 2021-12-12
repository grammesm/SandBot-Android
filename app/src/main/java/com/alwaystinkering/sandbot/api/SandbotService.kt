package com.alwaystinkering.sandbot.api

import com.alwaystinkering.sandbot.util.Annotations.Str
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SandbotService {

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

    @POST("/setled")
    fun setLed(@Body json: RequestBody): Call<ResponseBody>

    @GET("/filelist/")
    fun listFiles(): Call<FileListResult>

    @GET("/files/{fsName}/{fileName}")
    fun getParametricFile(@Path("fsName") fsName: String, @Path("fileName") fileName: String): Call<ParametricFile>

    @Str
    @GET("/files/{fsName}/{fileName}")
    fun getThetaRhoFile(@Path("fsName") fsName: String, @Path("fileName") fileName: String): Call<String>

    @Str
    @GET("/files/{fsName}/{fileName}")
    fun getSequenceFile(@Path("fsName") fsName: String, @Path("fileName") fileName: String): Call<String>

    @GET("/playFile/{fsName}/{fileName}")
    fun playFile(@Path("fsName") fsName: String, @Path("fileName") name: String): Call<CommandResult>

    @Multipart
    @POST("/uploadtofileman")
    fun saveFile(@Part filePart: MultipartBody.Part): Call<ResponseBody>

    @GET("/deleteFile/{fsName}/{fileName}")
    fun deleteFile(@Path("fsName") fsName: String, @Path("fileName") name: String): Call<CommandResult>

}