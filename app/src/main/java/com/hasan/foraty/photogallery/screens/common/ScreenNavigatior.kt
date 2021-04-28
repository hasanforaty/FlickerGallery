package com.hasan.foraty.photogallery.screens.common

import androidx.appcompat.app.AppCompatActivity

class ScreenNavigatior(private val activity: AppCompatActivity) {
    fun rollBack(){
        activity.onBackPressed()
    }

}