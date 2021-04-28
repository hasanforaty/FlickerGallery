package com.hasan.foraty.photogallery.screens.photogallery

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.hasan.foraty.photogallery.data.FlickrFetchrRepository
import com.hasan.foraty.photogallery.data.GalleryItem
import com.hasan.foraty.photogallery.data.QueryPreference

private const val TAG = "PhotoGalleryViewModel"
class PhotoGalleryViewModel(private val app:Application) :AndroidViewModel(app) {
    private val repository = FlickrFetchrRepository.newInstance()
    private val searchString: MutableLiveData<String> = MutableLiveData()
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    val searchText:String
        get() = searchString.value ?:""
    init {
        val storedQuery= QueryPreference.getStoredQuery(app)
        searchString.value = storedQuery
        galleryItemLiveData = Transformations
                .switchMap(searchString) { query ->
                    if (query.isEmpty()){
                        repository.fetchPhoto()
                    }else{
                        repository.searchPhotos(query)
                    }
                }
    }


    fun searchPhoto(query: String = "") {
        QueryPreference.setStoreQuery(app,query)
        searchString.value=query
    }
}