package com.example.vooov.repositories

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.vooov.data.dataInterfaces.PicturesRetrofitInterface
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class PicturesRepository {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://vooov-api.fr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val upload = retrofit.create(PicturesRetrofitInterface::class.java)

    suspend fun uploadImage(pathToImage: String) {
        val file = File(pathToImage)

        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        try {
            val response = upload.uploadImage(part)
            if (response.isSuccessful) {
                // Handle successful response
            } else {
                // Handle error
            }
        } catch (e: Exception) {
            // Handle failure
        }
    }

    private val download = retrofit.create(PicturesRetrofitInterface::class.java)
    var picture: String? = null
    suspend fun downloadImage(fileName: String): Bitmap? {
        try {
            val response = download.downloadImage(fileName)
            if (response.isSuccessful) {
                 picture = response.body()?.string()
                if (picture != null && Base64.decode( picture, Base64.NO_PADDING).isNotEmpty()) {
                    val imageBytes = Base64.decode( picture, Base64.NO_PADDING)

                    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } else {
                    Log.i(ContentValues.TAG, "Base64 string is empty or invalid")
                }
            } else {
                Log.i(ContentValues.TAG, "HTTP response code: ${response.code()}")
            }
        } catch (e: Exception){
            Log.i(ContentValues.TAG, "Message: ${e.message}")
        }
        return null
    }


}
