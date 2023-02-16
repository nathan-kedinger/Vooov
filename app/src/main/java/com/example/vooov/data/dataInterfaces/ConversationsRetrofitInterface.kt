package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.ConversationsModel
import retrofit2.Response
import retrofit2.http.*

interface ConversationsRetrofitInterface {
    @POST("conversations/create.php")
    suspend fun postConversation(@Body data: ConversationsModel): Response<ConversationsModel>

    @GET("conversations/read.php")
    suspend fun getConversations(@Query("uuid") uuid: String?): Response<MutableList<ConversationsModel>>

    @GET("conversations/readOne.php")
    suspend fun getOneConversation(@Query("uuid") uuid: String?): Response<ConversationsModel>

    @PUT("conversations/update.php/{uuid}")
    suspend fun updateConversation(@Path("uuid") uuid: String?, @Body conversations: ConversationsModel): Response<ConversationsModel>

    @DELETE("conversations/delete.php")
    suspend fun deleteConversation(@Query("uuid") uuid: String): Response<ConversationsModel>
}