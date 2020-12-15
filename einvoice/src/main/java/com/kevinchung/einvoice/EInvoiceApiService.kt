package com.kevinchung.einvoice

import android.os.Build
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EInvoiceApiService {
    companion object {
        private const val BASE_URL = Const.API_EINV_HOST

        private val loggingInterceptor : HttpLoggingInterceptor =
            run {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                if(BuildConfig.DEBUG)  httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                else httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                httpLoggingInterceptor
            }
        private val httpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
        private val retrofit = builder.build()

        fun <S> createService(serviceClass:Class<S>):S {
            return retrofit.create(serviceClass)
        }
    }
}