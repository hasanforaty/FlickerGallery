package com.hasan.foraty.photogallery.screens.common.fragment

import androidx.fragment.app.Fragment
import com.hasan.foraty.photogallery.common.composition.PresentationCompostionRoot
import com.hasan.foraty.photogallery.screens.common.activity.BaseActivity

open class BaseFragment:Fragment() {

    protected val compositionRoot by lazy {
        PresentationCompostionRoot((activity as BaseActivity).activityCompostionRoot)
    }

}