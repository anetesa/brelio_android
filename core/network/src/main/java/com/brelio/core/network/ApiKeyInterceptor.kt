package com.brelio.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor(
    @Named("supabase_anon_key") private val anonKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("apikey", anonKey)
            .build()
        return chain.proceed(request)
    }
}
