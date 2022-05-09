package com.example.ayu.regitest.Firebase

import com.example.ayu.regitest.Firebase.Constants.Companion.FCM_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(FCM_URL)
                .client(provideOkHttpClient(AppInterceptor()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val api : FcmInterface by lazy {
        retrofit.create(FcmInterface::class.java)
    }

    // Client
    private fun provideOkHttpClient(
            interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
            .run {
                addInterceptor(interceptor)
                build()
            }

    // 헤더 추가
    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain)
                : Response = with(chain) {
            val newRequest = request().newBuilder()
                    .addHeader("Authorization", "key=AAAAjYuNZuw:APA91bHJMu1uAmplw1odtxbQHhKoFTkfXrqgBVe5HW1l66OBOTNyZk1yBf6ThOUrnpMGeXlWlSVS6JDJlcAgMh34y21Jtdqw2zktkt5yuQua5fI7bIzuhTzzRFz2J_82egLNxZR_VJ0c")
                    .addHeader("Content-Type", "application/json")
                    .build()
            proceed(newRequest)
        }
    }
}