package com.example.vooov.repositories

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.vooov.data.dataInterfaces.PicturesRetrofitInterface
import com.example.vooov.data.dataInterfaces.RecordsRetrofitInterface
import com.example.vooov.data.dataInterfaces.UsersRetrofitInterface
import com.example.vooov.data.model.RecordModel
import com.example.vooov.data.model.UserModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

class RecordRepository {

    val retrofit = Retrofit.Builder()// Construction du client Retrofit
        .baseUrl("https://vooov-api.fr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val create: RecordsRetrofitInterface = retrofit.create(RecordsRetrofitInterface::class.java)

    suspend fun createRecordData(record: RecordModel): Response<RecordModel> {
        return try{
            create.postRecord(record)
        } catch (e: Exception) {
            throw IOException("Error creating record",e)
        }
    }

    private val read: RecordsRetrofitInterface = retrofit.create(RecordsRetrofitInterface::class.java)

    suspend fun readRecordData(): Response<MutableList<RecordModel>> {
        return try {
            read.getRecords()
        } catch(e: Exception){
            throw IOException("Error fetching record", e)
        }
    }

    private val readOne: RecordsRetrofitInterface = retrofit.create(RecordsRetrofitInterface::class.java)

    suspend fun readOneRecordData(userUuid: String): Response<RecordModel> {
        return try {
            readOne.getOneRecord(userUuid)
        } catch (e: Exception) {
            throw IOException("Error fetching record", e)
        }
    }

    private val update: RecordsRetrofitInterface = retrofit.create(RecordsRetrofitInterface::class.java)

    suspend fun updateRecordData(record: RecordModel): Response<RecordModel> {
        return try {
            update.updateRecord(record)
        } catch (e: Exception) {
            throw IOException("Error updating record", e)
        }
    }

    private val delete: RecordsRetrofitInterface = retrofit.create(RecordsRetrofitInterface::class.java)

    suspend fun deleteRecordData(recordUuid: String): Response<RecordModel> {
        return try {
            delete.deleteRecord(recordUuid)
        } catch (e: Exception) {
            throw IOException("Error deleting record", e)
        }
    }

    private val upload = retrofit.create(RecordsRetrofitInterface::class.java)


    suspend fun uploadRecord(pathToRecord: String) {
        val file = File(pathToRecord)

        // Create a RequestBody object from the file to be uploaded
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        // Create a MultipartBody.Part object to hold the file
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        try {
            // Call the uploadRecordFile method on the interface created by Retrofit
            val response = upload.uploadRecordFile(part)

            if (response.isSuccessful) {
                // Upload the record data if the response was successful
            } else {
                // Handle error
            }
        } catch (e: IOException) {
            // Handle failure due to a network error, such as a timeout or a lost connection
        } catch (e: IllegalStateException) {
            // Handle failure due to improper request parameters, such as missing required fields
        } catch (e: Exception) {
            // Handle any other failures that may occur
        }
    }
    // Constant for the log tag
    private val LOG_TAG = "PictureDownloader"

    // Service interface for downloading record
    private val downloadService = retrofit.create(RecordsRetrofitInterface::class.java)

    // Variable to store the downloaded record
    private var picture: String? = null

    // Suspend function to download the image with the given file name
    suspend fun downloadRecord(fileName: String): Bitmap? {
        try {
            // Make the HTTP request to download the record
            val response = downloadService.downloadRecordFile(fileName)

            // Check if the request was successful
            if (response.isSuccessful) {
                picture = response.body()?.string()
                // Check if the record data is not null and not an empty string
                if (picture != null && Base64.decode( picture, Base64.NO_PADDING).isNotEmpty()) {
                    val imageBytes = Base64.decode( picture, Base64.NO_PADDING)

                    // Convert the byte array to a Bitmap and return it
                    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } else {
                    // Log an error if the record data is invalid
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

        // Return null if the record could not be downloaded
        return null
    }
}