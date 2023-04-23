package com.example.vooov.repositories

import com.example.vooov.data.dataInterfaces.UsersRetrofitInterface
import com.example.vooov.data.model.UserModel
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class UserRepository {
    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://vooov.fr/public/api_vooov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val create: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun createUserData(user: UserModel): Response<UserModel> {
        return try{
            create.postUser(user)
        } catch (e: Exception) {
            throw IOException("Error creating user",e)
        }
    }

    private val read: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun readUserData(): Response<MutableList<UserModel>> {
        return try {
            read.getUsers()
        } catch(e: Exception){
            throw IOException("Error fetching user", e)
        }
    }

    private val readOne: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun readOneUserData(userUuid: String?): Response<UserModel> {
        return try {
            readOne.getOneUser(userUuid)
        } catch (e: Exception) {
            throw IOException("Error fetching user", e)
        }
    }

    private val readOneById: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun readOneUserDataById(userId: Int?): Response<UserModel> {
        return try {
            readOneById.getOneUserById(userId)
        } catch (e: Exception) {
            throw IOException("Error fetching user", e)
        }
    }

    private val readOneByMail: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun readOneUserDataByMail(userMail: String): Response<UserModel> {
        return try {
            readOneByMail.getOneUserByMail(userMail)
        } catch (e: Exception) {
            throw IOException("Error fetching user", e)
        }
    }

    private val update: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun updateUserData(userUuid: String?, userModel: UserModel): Response<UserModel> {
        return try {
            update.updateUser(userUuid, userModel)
        } catch (e: Exception) {
            throw IOException("Error updating user", e)
        }
    }

    private val delete: UsersRetrofitInterface = retrofit.create(UsersRetrofitInterface::class.java)

    suspend fun deleteUserData(recordUuid: String): Response<UserModel> {
        return try {
            delete.deleteUser(recordUuid)
        } catch (e: Exception) {
            throw IOException("Error deleting user", e)
        }
    }
}