package com.example.vooov.repositories

import com.example.vooov.data.dataInterfaces.ConversationsRetrofitInterface
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.data.model.MessagesModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class ConversationsRepository {
    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://vooov.fr/public/api_vooov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val create: ConversationsRetrofitInterface = retrofit.create(ConversationsRetrofitInterface::class.java)

    suspend fun createConversationData(conversation: ConversationsModel): Response<ConversationsModel> {
        return try{
            create.postConversation(conversation)
        } catch (e: Exception) {
            throw IOException("Error creating user",e)
        }
    }

    private val read: ConversationsRetrofitInterface = retrofit.create(ConversationsRetrofitInterface::class.java)

    suspend fun readConversationData(selfUuid: String): Response<MutableList<ConversationsModel>> {
        return try {
            read.getConversations(selfUuid)
        } catch(e: Exception){
            throw IOException("Error fetching user", e)
        }
    }

    private val readOne: ConversationsRetrofitInterface = retrofit.create(ConversationsRetrofitInterface::class.java)

    suspend fun readOneConversationData(conversation: String?): Response<ConversationsModel> {
        return try {
            readOne.getOneConversation(conversation)
        } catch (e: Exception) {
            throw IOException("Error fetching user", e)
        }
    }

    private val readConversations: ConversationsRetrofitInterface = retrofit.create(ConversationsRetrofitInterface::class.java)

    suspend fun readUserConversationsData(userUuid: String?, userUuidSame: String): Response<MutableList<ConversationsModel>>  {
        return try {
            readConversations.getUserConversations(userUuid,userUuidSame)
        } catch (e: Exception) {
            throw IOException("Error fetching messages", e)
        }
    }

    private val update: ConversationsRetrofitInterface = retrofit.create(ConversationsRetrofitInterface::class.java)

    suspend fun updateConversationData(conversationUuid: String?, conversationModel: ConversationsModel): Response<ConversationsModel> {
        return try {
            update.updateConversation(conversationUuid, conversationModel)
        } catch (e: Exception) {
            throw IOException("Error updating user", e)
        }
    }

    private val delete: ConversationsRetrofitInterface = retrofit.create(ConversationsRetrofitInterface::class.java)

    suspend fun deleteConversationData(conversationUuid: String): Response<ConversationsModel> {
        return try {
            delete.deleteConversation(conversationUuid)
        } catch (e: Exception) {
            throw IOException("Error deleting user", e)
        }
    }
}