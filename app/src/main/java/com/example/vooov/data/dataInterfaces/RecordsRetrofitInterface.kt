package com.example.vooov.data.dataInterfaces

import com.example.vooov.data.model.RecordModel
import com.example.vooov.data.model.UserModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RecordsRetrofitInterface {
    @POST("records/create.php")
    suspend fun postRecord(@Body data: RecordModel): Response<RecordModel>

    @GET("records/read.php")
    suspend fun getRecords(): Response<MutableList<RecordModel>>

    @GET("records/readOneByUuid.php")
    suspend fun getOneRecord(@Query("uuid") uuid: String): Response<RecordModel>

    @PUT("records/update.php")
    suspend fun updateRecord(@Body record: RecordModel): Response<RecordModel>

    @DELETE("records/delete.php")
    suspend fun deleteRecord(@Query("uuid") uuid: String): Response<RecordModel>

    @POST("record_files/record_upload.php")
    suspend fun uploadRecordFile(@Part file: MultipartBody.Part): Response<ResponseBody>

    @GET("record_files/record_download.php")
    suspend fun downloadRecordFile(@Query("file") filename: String): Response<ResponseBody>

    @DELETE("record_files/delete.php")
    suspend fun deleteRecordFile(@Query("uuid") uuid: String): Response<RecordModel>
}
