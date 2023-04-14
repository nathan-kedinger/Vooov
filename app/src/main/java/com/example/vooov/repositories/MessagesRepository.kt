package com.example.vooov.repositories

import com.example.vooov.data.dataInterfaces.ConversationsRetrofitInterface
import com.example.vooov.data.dataInterfaces.MessagesRetrofitInterface
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.data.model.MessagesModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MessagesRepository {
    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://vooov.fr/public/api_vooov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val create: MessagesRetrofitInterface = retrofit.create(MessagesRetrofitInterface::class.java)

    suspend fun createMessageData(message: MessagesModel): Response<MessagesModel> {
        return try{
            create.postMessage(message)
        } catch (e: Exception) {
            throw IOException("Error creating message",e)
        }
    }

    private val read: MessagesRetrofitInterface = retrofit.create(
        MessagesRetrofitInterface::class.java)

    suspend fun readMessageData(): Response<MutableList<MessagesModel>> {
        return try {
            read.getMessages()
        } catch(e: Exception){
            throw IOException("Error fetching user", e)
        }
    }

    private val readOne: MessagesRetrofitInterface = retrofit.create(
        MessagesRetrofitInterface::class.java)

    suspend fun readOneConversationMessagesData(message: String?): Response<MutableList<MessagesModel>>  {
        return try {
            readOne.getOneConversationMessages(message)
        } catch (e: Exception) {
            throw IOException("Error fetching messages", e)
        }
    }

    private val update: MessagesRetrofitInterface = retrofit.create(
        MessagesRetrofitInterface::class.java)

    suspend fun updateMessageData(messageUuid: String?, messagesModel: MessagesModel): Response<MessagesModel> {
        return try {
            update.updateMessage(messageUuid, messagesModel)
        } catch (e: Exception) {
            throw IOException("Error updating user", e)
        }
    }

    private val delete: MessagesRetrofitInterface = retrofit.create(
        MessagesRetrofitInterface::class.java)

    suspend fun deleteMessageData(messageUuid: String): Response<MessagesModel> {
        return try {
            delete.deleteMessage(messageUuid)
        } catch (e: Exception) {
            throw IOException("Error deleting user", e)
        }
    }
}