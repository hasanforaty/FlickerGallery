package com.hasan.foraty.photogallery.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hasan.foraty.photogallery.data.FlickrFetchrRepository
import com.hasan.foraty.photogallery.data.GalleryItem

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData:LiveData<List<GalleryItem>> = FlickrFetchrRepository.newInstance().fetchPhoto()
}