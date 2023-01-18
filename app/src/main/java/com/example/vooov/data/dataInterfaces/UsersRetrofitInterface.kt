package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.UserModel
import retrofit2.Response
import retrofit2.http.*

interface UsersRetrofitInterface {
        @POST("users/create.php")
        suspend fun postUser(@Body data: UserModel): Response<UserModel>

        @GET("users/read.php")
        suspend fun getUsers(): Response<MutableList<UserModel>>

        @GET("users/readOne.php")
        suspend fun getOneUser(@Query("uuid") uuid: String): Response<UserModel>

        @PUT("users/update.php")
        suspend fun updateUser(@Body record: UserModel): Response<UserModel>

        @DELETE("users/delete.php")
        suspend fun deleteUser(@Query("uuid") uuid: String): Response<UserModel>
}