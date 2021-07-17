package com.alwaystinkering.sandbot.data

import com.alwaystinkering.sandbot.utils.StringOrJsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


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

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://$ip")
            .addConverterFactory(StringOrJsonConverterFactory())
            .client(okHttpClient)
            .build()

        return retrofit!!.create(SandBotInterface::class.java)
    }
}
