package com.alwaystinkering.sandbot.repo

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HostSelectionInterceptor @Inject constructor(val preferencesManager: SharedPreferencesManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val host: String = preferencesManager.getServeIpAddress()

        val newUrl = request.url.newBuilder()
            .host(host)
            .build()

        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }

}