package com.example.vooov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vooov.databinding.ActivityHomeBinding
import com.example.vooov.databinding.ActivitySettingsBinding
import com.example.vooov.viewModels.CurrentUser

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.settingsLogout.setOnClickListener {
            CurrentUser(this).logout()
        }
    }
}