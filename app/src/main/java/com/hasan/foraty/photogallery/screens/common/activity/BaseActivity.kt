package com.hasan.foraty.photogallery.screens.common.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.hasan.foraty.photogallery.PhotoGalleryApplication
import com.hasan.foraty.photogallery.common.composition.ActivityCompositionRoot
import com.hasan.foraty.photogallery.common.composition.PresentationCompostionRoot

open class BaseActivity: AppCompatActivity() {
    private val appCompostionRoot get() =(application as PhotoGalleryApplication).appCompositionRoot

    val activityCompostionRoot by lazy {
        ActivityCompositionRoot(this,appCompostionRoot)
    }
    protected val compostionRoot by lazy {
        PresentationCompostionRoot(activityCompostionRoot)
    }
}