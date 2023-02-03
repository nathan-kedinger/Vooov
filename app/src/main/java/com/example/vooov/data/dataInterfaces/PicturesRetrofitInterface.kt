package com.example.vooov.data.dataInterfaces

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PicturesRetrofitInterface {
    @POST("profile_picture_files/picture_upload.php")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ResponseBody>

    @GET("profile_picture_files/picture_download.php")
    suspend fun downloadImage(@Query("file") filename: String): Response<ResponseBody>
}