package com.hasan.foraty.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData:LiveData<List<GalleryItem>> = FlickrFetchrRepository.newInstance().fetchPhoto()
}