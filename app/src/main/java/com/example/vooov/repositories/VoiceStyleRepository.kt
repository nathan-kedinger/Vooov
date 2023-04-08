package com.example.vooov.repositories

import com.example.vooov.data.dataInterfaces.VoiceStyleRetrofitInterface
import com.example.vooov.data.model.VoiceStyleModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class VoiceStyleRepository {
    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://vooov.fr/public/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val read: VoiceStyleRetrofitInterface = retrofit.create(VoiceStyleRetrofitInterface::class.java)

    suspend fun readVoiceStylesData(): Response<MutableList<VoiceStyleModel>> {
        return try {
            read.getVoiceStyles()
        } catch(e: Exception){
            throw IOException("Error fetching voiceStyle", e)
        }
    }

    private val readOne: VoiceStyleRetrofitInterface = retrofit.create(VoiceStyleRetrofitInterface::class.java)

    suspend fun readOneVoiceStyleData(voiceStyleId: Int?): Response<VoiceStyleModel> {
        return try {
            readOne.getOneVoiceStyle(voiceStyleId)
        } catch (e: Exception) {
            throw IOException("Error fetching voiceStyle", e)
        }
    }
}