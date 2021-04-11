package com.hasan.foraty.photogallery.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hasan.foraty.photogallery.data.FlickrFetchrRepository
import com.hasan.foraty.photogallery.data.GalleryItem

class PhotoGalleryViewModel : ViewModel() {
    private val repository = FlickrFetchrRepository.newInstance()
    private val searchString: MutableLiveData<String> = MutableLiveData()
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        searchString.value = "planets"
        galleryItemLiveData = Transformations
                .switchMap(searchString) { query ->
                    repository.searchPhotos(query)
                }
    }


    fun searchPhoto(query: String = "") {
        searchString.value=query
    }
}