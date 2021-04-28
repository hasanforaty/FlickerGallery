package com.hasan.foraty.photogallery.common.composition

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.hasan.foraty.photogallery.screens.common.activity.BaseActivity

class ActivityCompositionRoot(val activity: AppCompatActivity, appCompostionRoot: AppCompostionRoot) {

    val layoutInflater get() = LayoutInflater.from(activity)
}