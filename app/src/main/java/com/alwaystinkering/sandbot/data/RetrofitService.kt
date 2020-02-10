package com.alwaystinkering.sandbot.data

import com.alwaystinkering.sandbot.utils.StringOrJsonConverterFactory
import com.google.gson.GsonBuilder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitService {

    var sandBotInterface: SandBotInterface? = null
        private set

    private var retrofit: Retrofit? = null// = new Retrofit.Builder()
    //            .baseUrl("http://192.168.1.170")
    //            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
    //                    .setLenient()
    //                    .create()))
    //            .build();

    fun createService(ip: String): SandBotInterface {
        retrofit = Retrofit.Builder()
            .baseUrl("http://$ip")
            .addConverterFactory(StringOrJsonConverterFactory())
            .build()

        return retrofit!!.create(SandBotInterface::class.java)
    }
}
