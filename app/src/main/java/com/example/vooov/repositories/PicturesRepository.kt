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
        .baseUrl("https://vooov.fr/public/")
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

    // Constant for the log tag
    private val LOG_TAG = "PictureDownloader"

    // Service interface for downloading images
    private val downloadService = retrofit.create(PicturesRetrofitInterface::class.java)

    // Variable to store the downloaded picture
    private var picture: String? = null

    // Suspend function to download the image with the given file name
    suspend fun downloadImage(fileName: String): Bitmap? {
        try {
            // Make the HTTP request to download the image
            val response = downloadService.downloadImage(fileName)

            // Check if the request was successful
            if (response.isSuccessful) {
                picture = response.body()?.string()
                // Check if the picture data is not null and not an empty string
                if (picture != null && Base64.decode( picture, Base64.NO_PADDING).isNotEmpty()) {
                    val imageBytes = Base64.decode( picture, Base64.NO_PADDING)

                    // Convert the byte array to a Bitmap and return it
                    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } else {
                    // Log an error if the picture data is invalid
                    Log.i(LOG_TAG, "Base64 string is empty or invalid")
                }
            } else {
                // Log an error if the HTTP request was not successful
                Log.i(LOG_TAG, "HTTP response code: ${response.code()}")
            }
        } catch (e: Exception) {
            // Log an error if an exception was thrown
            Log.i(LOG_TAG, "Message: ${e.message}")
        }

        // Return null if the image could not be downloaded
        return null
    }


}
