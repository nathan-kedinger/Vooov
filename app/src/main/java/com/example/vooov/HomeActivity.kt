package com.example.vooov

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.vooov.databinding.ActivityHomeBinding
import com.example.vooov.repositories.PicturesRepository
import com.example.vooov.viewModels.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    // MainFragment actions
    private var MainFragmentOn : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // To personal profile fragment
        binding.homeMainProfil.setOnClickListener{
            this.findNavController(R.id.nav_host_fragment).navigate(R.id.personalProfileFragment)
            MainFragmentOn = false
        }

        val currentUserId = CurrentUser(this).readString("uuid")

        // To other activities
        binding.homeMainRecordRecord.setOnClickListener{
            // Check if user is signed in.
            if (!CurrentUser(this).connected) {
                LoginPopup(this).show()



            } else {
                startActivity(Intent(this, StudioActivity::class.java))

                finish()
            }
        }
    }
}