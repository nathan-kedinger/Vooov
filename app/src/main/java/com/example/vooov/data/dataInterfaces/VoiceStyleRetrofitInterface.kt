package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.VoiceStyleModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VoiceStyleRetrofitInterface {

    @GET("voice_style/read.php")
    suspend fun getVoiceStyles(): Response<MutableList<VoiceStyleModel>>

    @GET("voice_style/readOne.php")
    suspend fun getOneVoiceStyle(@Query("id") id: Int?): Response<VoiceStyleModel>
}