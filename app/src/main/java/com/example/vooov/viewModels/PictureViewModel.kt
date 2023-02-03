package com.example.vooov.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vooov.repositories.PicturesRepository

class PictureViewModel: ViewModel() {

    private val repository = PicturesRepository()


    fun downloadImage(fileName: String){

    }

}