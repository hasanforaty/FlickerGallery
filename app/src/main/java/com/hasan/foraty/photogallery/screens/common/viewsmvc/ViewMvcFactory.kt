package com.hasan.foraty.photogallery.screens.common.viewsmvc

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hasan.foraty.photogallery.screens.photogallery.PhotoGalleryMvc

class ViewMvcFactory(private val layoutInflater: LayoutInflater) {

    fun newPhotoGalleryMvc(parent: ViewGroup?):PhotoGalleryMvc{
        return PhotoGalleryMvc(layoutInflater,parent)
    }
}