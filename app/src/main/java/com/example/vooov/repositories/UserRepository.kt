package com.example.vooov.repositories

import com.example.vooov.data.dataInterfaces.UsersRetrofitInterface
import com.example.vooov.data.model.UserModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class UserRepository {

    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://bts-sio-kedinger.fr/vooov-api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val create: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun createUserData(user: UserModel): Response<UserModel> {
        return try{
            create.postUser(user)
        } catch (e: Exception) {
            throw IOException("Error creating record",e)
        }
    }

    private val read: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun readUserData(): Response<MutableList<UserModel>> {
        return try {
            read.getUsers()
        } catch(e: Exception){
            throw IOException("Error fetching record", e)
        }
    }

    private val readOne: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun readOneUserData(userUuid: String): Response<UserModel> {
        return try {
            readOne.getOneUser(userUuid)
        } catch (e: Exception) {
            throw IOException("Error fetching record", e)
        }
    }

    private val update: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun updateUserData(record: UserModel): Response<UserModel> {
        return try {
            update.updateUser(record)
        } catch (e: Exception) {
            throw IOException("Error updating record", e)
        }
    }

    private val delete: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun deleteUserData(recordUuid: String): Response<UserModel> {
        return try {
            delete.deleteUser(recordUuid)
        } catch (e: Exception) {
            throw IOException("Error updating record", e)
        }
    }}