package com.example.ayu.regitest.Firebase

import com.example.ayu.regitest.Firebase.NotificationBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmInterface {
    @POST("fcm/send")
    suspend fun sendNotification(
            @Body notification: NotificationBody
    ) : Response<ResponseBody>
}