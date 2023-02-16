package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.MessagesModel
import retrofit2.Response
import retrofit2.http.*

interface MessagesRetrofitInterface {
    @POST("messages/create.php")
    suspend fun postMessage(@Body data: MessagesModel): Response<MessagesModel>

    @GET("messages/read.php")
    suspend fun getMessages(): Response<MutableList<MessagesModel>>

    @GET("messages/readOne.php")
    suspend fun getOneConversationMessages(@Query("conversation_uuid") conversation_uuid: String?): Response<MutableList<MessagesModel>>

    @PUT("messages/update.php/{uuid}")
    suspend fun updateMessage(@Path("uuid") uuid: String?, @Body message: MessagesModel): Response<MessagesModel>

    @DELETE("messages/delete.php")
    suspend fun deleteMessage(@Query("uuid") uuid: String): Response<MessagesModel>
}