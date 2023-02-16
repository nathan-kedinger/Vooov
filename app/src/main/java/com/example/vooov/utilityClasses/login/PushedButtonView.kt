package com.example.vooov.utilityClasses.login

import android.widget.ImageButton

class PushedButtonView {
    lateinit var  button : ImageButton

    fun changeImageButtonOnPush(button: ImageButton, state : Boolean, firstStateIcon: Int, secondStateIcon : Int){
        when(state){
            true-> button.setBackgroundResource(secondStateIcon)
            false-> button.setBackgroundResource(firstStateIcon)
        }
    }

}
