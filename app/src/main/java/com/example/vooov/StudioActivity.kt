package com.example.vooov

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.vooov.data.model.AudioRecordCategoriesModel
import com.example.vooov.data.model.RecordModel
import com.example.vooov.data.model.VoiceStyleModel
import com.example.vooov.databinding.ActivityStudioBinding
import com.example.vooov.repositories.RecordRepository
import com.example.vooov.viewModels.AudioRecordCategoriesViewModel
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.VoiceStyleViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class StudioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudioBinding

    private var fileFullPath: String = ""

    private var recorder: MediaRecorder? = null
    private var mStartRecording = true

    private var player: MediaPlayer? = null
    private var mStartPlaying = true

    val randomId = UUID.randomUUID().toString()

    // Time counting
    private var startTime: Long = 0
    private var stopTime: Long = 0



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
                fileFullPath = "${filesDir.absolutePath}/${randomId}.mp4"
                Log.i(ContentValues.TAG, "file path: $fileFullPath")

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

                val rewindButton = binding.studioRewindButton
                rewindButton.setOnClickListener {
                    reinitializeMeterProgressTime()
                }

                val publishButton: ImageButton = binding.studioPublishButton
                publishButton.setOnClickListener {
                    publish()
                }

                loadCategories()
                loadVoiceStyles()
            }

    private fun loadCategories() {
        val audioRecordCategoriesViewModel = AudioRecordCategoriesViewModel()
        lifecycleScope.launch(Dispatchers.Main) {
            audioRecordCategoriesViewModel.fetchAudioRecordCategories()
            audioRecordCategoriesViewModel.audioRecordCategorieslist.observe(this@StudioActivity) { categories ->
                setupSpinnerCategories(categories)
            }
        }
    }

    private fun setupSpinnerCategories(categories: List<AudioRecordCategoriesModel>) {
        val spinner = binding.studioCategorie
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    private fun loadVoiceStyles(){
        val voiceStylesViewModel = VoiceStyleViewModel()
        lifecycleScope.launch  (Dispatchers.Main){
            voiceStylesViewModel.fetchVoiceStyles()
            voiceStylesViewModel.voiceStylelist.observe(this@StudioActivity){ voiceStyle ->
                setupSpinnerVoiceStyles(voiceStyle)
            }
        }
    }

    private fun setupSpinnerVoiceStyles(voiceStyles: List<VoiceStyleModel>){
        val spinner = binding.studioVoiceType
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voiceStyles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    fun publish(){
        val repo = RecordsViewModel()
        val recordArtistId  = CurrentUser(this).id
        val recordTitle = binding.studioRecordName.text.toString()
        val length = (getTotalRecordTimeLength() / 1000).toString().toInt()
        val selectedVoiceType = binding.studioVoiceType.selectedItem as VoiceStyleModel
        val recordVoiceType = selectedVoiceType.id
        val selectedCategory = binding.studioCategorie.selectedItem as AudioRecordCategoriesModel
        val recordCategory = selectedCategory.id
        val description = ""
        val created_at = Date().toString()
        val updated_at = Date().toString()


        // Launch a coroutine with the main dispatcher
        CoroutineScope(Dispatchers.Main).launch {
            // Switch to the IO dispatcher
            withContext(Dispatchers.IO) {
                val record = RecordModel(
                    null,
                    recordArtistId,
                    recordCategory,
                    recordVoiceType,
                    randomId,
                    recordTitle,
                    length,
                    0,
                    0,
                    description,
                    created_at,
                    updated_at
                )

                // Call createRecord function of the repo object with the record parameters
                repo.createRecord(record)
                // Call the uploadRecord function of the RecordRepository object with the fileFullPath and randomId parameters
                RecordRepository().uploadRecord(fileFullPath, randomId)
                startActivity(Intent(this@StudioActivity, StudioActivity::class.java))

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
        pausePlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileFullPath)
                prepare()
                startTimer()
                start()
                startTimeCounting()
                startMeterProgressTime()
                CoroutineScope(Dispatchers.Main).launch {
                    stopMeterProgressTimeWhenFinish()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun pausePlaying() {
        player?.pause()
        stopTimeCounting()
        stopMeterProgressTime()
        stopTimer()
    }

    private fun stopPlaying() {
        stopMeterTotalTime()
        player?.release()
        player = null
        stopMeterProgressTime()
        stopTimer()
        binding.studioSeekBar.progress = 0
    }

    private fun startTimeCounting() {
        startTime = System.currentTimeMillis()
    }

    private fun stopTimeCounting() {
        stopTime = System.currentTimeMillis()
    }

    private fun getTotalRecordTimeLength(): Long {
        return stopTime - startTime
    }

    private fun startMeterTotalTime() {
        binding.studioMeterTotalTime.start()
    }

    private fun stopMeterTotalTime() {
        binding.studioMeterTotalTime.stop()
    }

    private fun reinitializeMeterTotalTime() {
        binding.studioMeterTotalTime.base = SystemClock.elapsedRealtime()
    }

    private var elapsedTime: Long = 0
    private var isRunning: Boolean = false

    private fun startMeterProgressTime() {
        if (!isRunning) {
            binding.studioMeterProgressingTime.base = SystemClock.elapsedRealtime() - elapsedTime
            binding.studioMeterProgressingTime.start()
            isRunning = true
        }
    }

    private fun stopMeterProgressTime() {
        if (isRunning) {
            elapsedTime = SystemClock.elapsedRealtime() - binding.studioMeterProgressingTime.base
            binding.studioMeterProgressingTime.stop()
            isRunning = false
        }
    }

    private fun reinitializeMeterProgressTime() {
        binding.studioMeterProgressingTime.base = SystemClock.elapsedRealtime()
        elapsedTime = 0
        binding.studioSeekBar.progress = 0
        onPlay(start = false)
        binding.studioPlayButton.setBackgroundResource(R.drawable.ic_play)
    }

    private fun stopMeterProgressTimeWhenFinish() {
        player?.setOnCompletionListener {
            stopMeterProgressTime()
            stopTimer()
            binding.studioSeekBar.progress = binding.studioSeekBar.max
            binding.studioPlayButton.setBackgroundResource(R.drawable.ic_play)
        }
    }
    private var timer = Timer()

    private fun startTimer() {
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
                binding.studioSeekBar.progress = progress
            }
        }, 100, 100)

        binding.studioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player?.seekTo(progress * 100)

                    val duration = player?.duration ?: 0
                    val time = duration * progress / 100
                    binding.studioMeterProgressingTime.text = formatTime(time.toLong() )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    private fun stopTimer() {
        timer.cancel()
        binding.studioSeekBar.progress = 1
    }
    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun startRecording() {
        /* appui sur le bouton enregistrement : remise à zéro du compteur
                                                départ  des deux chronos
                                                visuel de bouton de déclanchement
                                                arrêt de la lecture du son si toujours en cours

                                                enregistrement bloqué quand  téléchargements lancés
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Create a new instance of MediaRecorder with the current context as parameter
            recorder = MediaRecorder(this).apply {
                // Set the audio source to default
                setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                // Set the output format to MPEG-4
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                // Set the output file path
                setOutputFile(fileFullPath)
                // Set the audio encoder to HE_AAC
                setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
                startTimeCounting()
                reinitializeMeterTotalTime()
                reinitializeMeterProgressTime()
                startMeterTotalTime()
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
                setOutputFile(fileFullPath)
                setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
                startTimeCounting()
                reinitializeMeterTotalTime()
                reinitializeMeterProgressTime()
                startMeterTotalTime()

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
            stopTimeCounting()
            stopMeterTotalTime()
        }
        recorder = null
    }

    override fun onPause() {
        super.onPause()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
        binding.studioMeterProgressingTime.stop()
    }
    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
        binding.studioMeterProgressingTime.stop()
    }
}