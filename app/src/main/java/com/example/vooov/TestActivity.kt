package com.example.vooov

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.vooov.databinding.ActivityLoginBinding
import com.example.vooov.databinding.ActivityTestBinding
import com.example.vooov.repositories.PicturesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var image = binding.imageView
        val repo = PicturesRepository()
        binding.testButton.setOnClickListener {

            startActivity(Intent(this, StudioActivity::class.java))

            }

        CoroutineScope(Dispatchers.Main).launch {
            val limage = repo.downloadImage("logo_vooov_small.png")
            image.setImageBitmap(limage)
        }


    }
}