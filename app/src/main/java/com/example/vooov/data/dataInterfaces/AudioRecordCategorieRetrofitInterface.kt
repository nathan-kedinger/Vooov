package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.AudioRecordCategoriesModel
import com.example.vooov.data.model.VoiceStyleModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AudioRecordCategorieRetrofitInterface {
    @GET("audio_record_categories/read.php")
    suspend fun getAudioRecordCategories(): Response<MutableList<AudioRecordCategoriesModel>>

    @GET("audio_record_categories/readOne.php")
    suspend fun getOneAudioRecordCategory(@Query("id") id: Int?): Response<AudioRecordCategoriesModel>
}