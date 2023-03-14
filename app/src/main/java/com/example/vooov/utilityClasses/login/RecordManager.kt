package com.example.vooov.utilityClasses.login

import android.content.Context
import android.media.MediaPlayer
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.SeekBar
import com.example.vooov.R
import com.example.vooov.repositories.RecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class RecordManager {

    //play bloc
    private var player: MediaPlayer? = null
    val repo = RecordRepository()

    // Time counting
    private var startTime: Long = 0
    private var stopTime: Long = 0


    suspend fun onPlay(start: Boolean, currentRecordUuid: String, context: Context, progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) = if(start) {
        startPlaying(currentRecordUuid, context, progressTimeChronometer, progressSeekBar)
    } else {
        pausePlaying(progressTimeChronometer, progressSeekBar)
    }

    suspend private fun startPlaying(currentRecordUuid: String, context: Context, progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) {
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
                        prepare()
                        startTimer(progressTimeChronometer, progressSeekBar)
                        start()
                        startTimeCounting()
                        startMeterProgressTime(progressTimeChronometer)
                        CoroutineScope(Dispatchers.Main).launch {
                            stopMeterProgressTimeWhenFinish(progressTimeChronometer, progressSeekBar)
                        }
                    } catch (e: Exception) {
                        throw IOException("Error reading record",e)
                    }
                }
            }
        }
    }


    private fun pausePlaying(progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) {
        player?.pause()
        stopTimeCounting()
        stopMeterProgressTime(progressTimeChronometer)
        stopTimer(progressSeekBar)
    }

    private fun stopPlaying(progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) {
        stopMeterTotalTime(progressTimeChronometer)
        player?.release()
        player = null
        stopMeterProgressTime(progressTimeChronometer)
        stopTimer(progressSeekBar)
        progressSeekBar.progress = 0
    }
    private fun startTimeCounting() {
        startTime = System.currentTimeMillis()
    }

    private fun stopTimeCounting() {
        stopTime = System.currentTimeMillis()
    }

    private fun getTotalRecordTimeLength(recordLength: Int): Long {
        return recordLength.toLong()
    }

    private fun stopMeterTotalTime(totalTimeChronometer: Chronometer) {
        totalTimeChronometer.stop()
    }

    private var elapsedTime: Long = 0
    private var isRunning: Boolean = false

    private fun startMeterProgressTime(progressTimeChronometer: Chronometer) {
        if (!isRunning) {
            progressTimeChronometer.base = SystemClock.elapsedRealtime() - elapsedTime
            progressTimeChronometer.start()
            isRunning = true
        }
    }

    private fun stopMeterProgressTime(progressTimeChronometer: Chronometer) {
        if (isRunning) {
            elapsedTime = SystemClock.elapsedRealtime() - progressTimeChronometer.base
            progressTimeChronometer.stop()
            isRunning = false
        }
    }

    private suspend fun reinitializeMeterProgressTime(progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) {
        progressTimeChronometer.base = SystemClock.elapsedRealtime()
        elapsedTime = 0
        progressSeekBar.progress = 0
    }

    private fun stopMeterProgressTimeWhenFinish(progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) {
        player?.setOnCompletionListener {
            stopMeterProgressTime(progressTimeChronometer)
            stopTimer(progressSeekBar)
            progressSeekBar.progress = progressSeekBar.max
        }
    }
    private var timer = Timer()

    private fun startTimer(progressTimeChronometer: Chronometer, progressSeekBar: SeekBar) {
        timer.cancel()
        val timer = Timer()
        startTime = 0 //System.currentTimeMillis()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val progress = player?.let {
                    if (it.duration > 0) {
                        it.currentPosition / (it.duration / 100)
                    } else {
                        0
                    }
                } ?: 0
                progressSeekBar.progress = progress
            }
        }, 100, 100)

        progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player?.seekTo(progress * 100)

                    val duration = player?.duration ?: 0
                    val time = duration * progress / 100
                    progressTimeChronometer.text = formatTime(time.toLong() )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    private fun stopTimer(progressSeekBar: SeekBar) {
        timer.cancel()
        progressSeekBar.progress = 1
    }
    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

}