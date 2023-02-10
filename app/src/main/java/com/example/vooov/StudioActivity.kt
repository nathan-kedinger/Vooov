package com.example.vooov

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.vooov.data.model.RecordModel
import com.example.vooov.databinding.ActivityStudioBinding
import com.example.vooov.repositories.RecordRepository
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.RecordsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class StudioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudioBinding

    private var fileName: String = ""

    private var recorder: MediaRecorder? = null
    private var mStartRecording = false

    private var player: MediaPlayer? = null
    private var mStartPlaying = true

    val randomId = UUID.randomUUID().toString()

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<String>,
                grantResults: IntArray
            ) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                } else {
                    false
                }
                if (!permissionToRecordAccepted) finish()
            }
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityStudioBinding.inflate(layoutInflater)
                setContentView(binding.root)

                binding.studioBackHome.setOnClickListener{
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                // Record to the external cache directory for visibility
                //fileName = "${externalCacheDir?.absolutePath}/ ${randomId}.3gp"
                fileName = "${filesDir.absolutePath}/${randomId}.mp4"
                Toast.makeText(this, fileName, Toast.LENGTH_LONG).show()


                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

                val recordButton: ImageButton = binding.studioRecordButton
                recordButton.setOnClickListener {

                    onRecord(mStartRecording)
                    when (mStartRecording) {
                        true -> recordButton.setBackgroundResource(R.drawable.ic_record_pushed)
                        false -> recordButton.setBackgroundResource(R.drawable.ic_record)
                    }

                    mStartRecording = !mStartRecording
                }

                val playButton: ImageButton = binding.studioPlayButton
                playButton.setOnClickListener {

                    onPlay(mStartPlaying)
                    when (mStartPlaying) {
                        true -> playButton.setBackgroundResource(R.drawable.ic_pause)
                        false -> playButton.setBackgroundResource(R.drawable.ic_play)
                    }
                    mStartPlaying = !mStartPlaying
                }


                val publishButton: ImageButton = binding.studioPublishButton
                publishButton.setOnClickListener {
                    publish()
                }
            }

            fun publish(){
                val repo = RecordsViewModel()
                val recordArtistUUID  = CurrentUser(this).uuid
                val recordTitle = binding.studioRecordName.text.toString()
                val length = 5
                val recordVoiceType = binding.studioVoiceType.selectedItem.toString()
                val recordKind = binding.studioCategorie.selectedItem.toString()
                val description = ""
                val created_at = Date().toString()
                val updated_at = Date().toString()
                val record = RecordModel(
                    randomId,
                    recordArtistUUID,
                    recordTitle,
                    length,
                    0,
                    0,
                    recordVoiceType,
                    recordKind,
                    description,
                    created_at,
                    updated_at
                )

                //repo.createRecord(record)
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        RecordRepository().uploadRecord(fileName)
                    }
                }

            }

            private fun onRecord(start: Boolean) = if (start) {
                startRecording()
            } else {
                stopRecording()
            }

            private fun onPlay(start: Boolean) = if (start) {
                startPlaying()
            } else {
                stopPlaying()
            }
            private fun startPlaying() {
                player = MediaPlayer().apply {
                    try {
                        setDataSource(fileName)
                        prepare()
                        start()

                    } catch (e: IOException) {
                        Log.e(LOG_TAG, "prepare() failed")
                    }
                }
            }

            private fun stopPlaying() {
                player?.release()
                player = null
            }

            private fun startRecording() {
                /* appui sur le bouton enregistrement : remise à zéro du compteur
                                                        bouton play sur play (pas sur pause)
                                                        départ  des deux chronos
                                                        visuel de bouton de déclanchement
                                                        arrêt de la lecture du son si toujours en cours

                                                        enregistrement bloqué quand  téléchargemetns lancés
                */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    recorder = MediaRecorder(this).apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setOutputFile(fileName)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        binding.studioMeterReading
                        binding.studioMeterTotalTime
                        try {
                            prepare()
                        } catch (e: IOException) {
                            Log.e(LOG_TAG, "prepare() failed")
                        }

                        start()
                    }
                } else {
                    recorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setOutputFile(fileName)
                        setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
                        binding.studioMeterReading
                        binding.studioMeterTotalTime
                        try {
                            prepare()
                        } catch (e: IOException) {
                            Log.e(LOG_TAG, "prepare() failed")
                        }

                        start()
                    }
                }
            }

            private fun stopRecording() {
                recorder?.apply {
                    stop()
                    release()
                }
                recorder = null
            }

            override fun onStop() {
                super.onStop()
                recorder?.release()
                recorder = null
                player?.release()
                player = null
            }
        }












