package com.brelio.core.network

import com.brelio.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(HEADER_API_KEY, BuildConfig.SUPABASE_ANON_KEY)
            .build()
        return chain.proceed(request)
    }

    private companion object {
        const val HEADER_API_KEY = "apikey"
    }
}
