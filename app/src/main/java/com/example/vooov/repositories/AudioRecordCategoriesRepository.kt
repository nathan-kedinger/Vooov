package com.example.vooov.repositories

import com.example.vooov.data.dataInterfaces.AudioRecordCategorieRetrofitInterface
import com.example.vooov.data.model.AudioRecordCategoriesModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class AudioRecordCategoriesRepository {
    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://vooov.fr/public/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val read: AudioRecordCategorieRetrofitInterface = retrofit.create(AudioRecordCategorieRetrofitInterface::class.java)

    suspend fun readAudioRecordCategoriesData(): Response<MutableList<AudioRecordCategoriesModel>> {
        return try {
            read.getAudioRecordCategories()
        } catch(e: Exception){
            throw IOException("Error fetching audioRecordCategories", e)
        }
    }

    private val readOne: AudioRecordCategorieRetrofitInterface = retrofit.create(AudioRecordCategorieRetrofitInterface::class.java)

    suspend fun readOneAudioRecordCategoryData(voiceStyleId: Int?): Response<AudioRecordCategoriesModel> {
        return try {
            readOne.getOneAudioRecordCategory(voiceStyleId)
        } catch (e: Exception) {
            throw IOException("Error fetching audioRecordCategory", e)
        }
    }
}