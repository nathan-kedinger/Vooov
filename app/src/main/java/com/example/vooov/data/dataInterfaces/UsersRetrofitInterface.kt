package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.UserModel
import retrofit2.Response
import retrofit2.http.*

interface UsersRetrofitInterface {
        @POST("users/create.php")
        suspend fun postUser(@Body data: UserModel): Response<UserModel>

        @GET("users/read.php")
        suspend fun getUsers(): Response<MutableList<UserModel>>

        @GET("users/readOneByUuid.php")
        suspend fun getOneUser(@Query("uuid") uuid: String?): Response<UserModel>

        @GET("users/readOneById.php")
        suspend fun getOneUserById(@Query("id") id: Int?): Response<UserModel>

        @GET("users/readOneByMail.php")
        suspend fun getOneUserByMail(@Query("email") email: String): Response<UserModel>

        @PUT("users/update.php/{uuid}")
        suspend fun updateUser(@Path("uuid") uuid: String?, @Body user: UserModel): Response<UserModel>

        @DELETE("users/delete.php")
        suspend fun deleteUser(@Query("uuid") uuid: String): Response<UserModel>
}