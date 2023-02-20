package com.example.vooov.utilityClasses.login

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.vooov.repositories.RecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class RecordManager {

    //play bloc
    private var player: MediaPlayer? = null
    val repo = RecordRepository()

    suspend fun onPlay(start: Boolean, currentRecordUuid: String, context: Context) = if(start) {
        startPlaying(currentRecordUuid, context)
    } else {
        stopPlaying()
    }

    suspend private fun startPlaying(currentRecordUuid: String, context: Context) {
        val audioRecord = repo.downloadAudioRecord("$currentRecordUuid.mp4")
        if (audioRecord != null) {
            // Save the downloaded audio file to a local file
            val file = File(context.filesDir, "$currentRecordUuid.mp4")
            file.writeBytes(audioRecord)
            Log.e("LOG_TAG", "le fichier existe-t-il? ${file.exists()}")

            Log.e("LOG_TAG", "$file")

            CoroutineScope(Dispatchers.Main).launch {
                // Initialize the media player with the local file path
                player = MediaPlayer().apply {
                    try {
                        setDataSource(file.absolutePath)

                        setOnPreparedListener {
                            start()
                        }

                        setOnErrorListener { _, what, extra ->
                            Log.e("LOG_TAG", "MediaPlayer error: what=$what, extra=$extra")
                            false
                        }

                        prepareAsync()
                    } catch (e: IOException) {
                        Log.e("LOG_TAG", e.fillInStackTrace().toString())
                        Log.e("LOG_TAG", e.printStackTrace().toString())
                    }
                }
            }
        }
    }


    private fun stopPlaying() {
        player?.release()
        player = null
    }

}