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
        .baseUrl("https://vooov.fr/public/")
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

    suspend fun readOneRecordData(recordId: Int): Response<RecordModel> {
        return try {
            readOne.getOneRecord(recordId)
        } catch (e: Exception) {
            throw IOException("Error fetching record", e)
        }
    }

    private val update: RecordsRetrofitInterface = retrofit.create(RecordsRetrofitInterface::class.java)

    suspend fun updateRecordData(recordUuid: String?, recordModel: RecordModel): Response<RecordModel> {
        return try {
            update.updateRecord(recordUuid, recordModel)
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


    suspend fun uploadRecord(pathToRecord: String, fileFullName: String) {
        val file = File(pathToRecord)

        // Create a RequestBody object from the file to be uploaded
        val fileName = RequestBody.create(MediaType.parse("text/plain"), "${fileFullName}.mp3")
        val mimeType = RequestBody.create(MediaType.parse("text/plain"), "audio/mpeg")
        val fileSize = RequestBody.create(MediaType.parse("text/plain"), file.length().toString())

        // Create a MultipartBody.Part object to hold the file
        val filepart = MultipartBody.Part.createFormData("file", file.name, RequestBody.create(MediaType.parse("audio/mpg"), file))


        try {
            // Call the uploadRecordFile method on the interface created by Retrofit
            val response = upload.uploadRecordFile(fileName, mimeType, fileSize, filepart)

            if (response.isSuccessful) {
                Log.d(TAG, "File uploaded successfully")
            } else {
                val responseCode = response.code()
                Log.d(TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
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
    private val LOG_TAG = "RecordDownloader"

    // Service interface for downloading record
    private val downloadService = retrofit.create(RecordsRetrofitInterface::class.java)

    // Variable to store the downloaded record
    private var picture: String? = null

    // Suspend function to download the record with the given file name
    suspend fun downloadAudioRecord(fileName: String): ByteArray? {
        try {
            // Make the HTTP request to download the audio record
            val response = downloadService.downloadRecordFile(fileName)
            // Check if the request was successful
            if (response.isSuccessful) {
                val audioData = response.body()?.bytes()
                // Check if the audio data is not null
                if (audioData != null && audioData.isNotEmpty()) {
                    // Return the audio data as a byte array
                    return audioData
                } else {
                    // Log an error if the audio data is invalid
                    Log.i(LOG_TAG, "Audio data is empty or invalid")
                }
            } else {
                // Log an error if the HTTP request was not successful
                Log.i(LOG_TAG, "HTTP response code: ${response.code()}")
                Log.i(LOG_TAG, "HTTP response fileName: ${fileName}")
            }
        } catch (e: Exception) {
            // Log an error if an exception was thrown
            Log.i(LOG_TAG, "Message: ${e.message}")
        }
        // Return null if the audio record could not be downloaded
        return null
    }
}