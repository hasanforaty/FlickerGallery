package com.hasan.foraty.photogallery.screens.photogallery

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.screens.common.viewsmvc.BaseViewMvc

private const val TAG = "PhotoGalleryMvc"
class PhotoGalleryMvc(
        private val layoutInflater: LayoutInflater,
        private val parent: ViewGroup?
) : BaseViewMvc<PhotoGalleryMvc.Listener>(
        layoutInflater, parent, R.layout.fragment_photo_gallery) {
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoGalleryViewModel:PhotoGalleryViewModel
    private lateinit var gridLayoutManager: GridLayoutManager

    interface Listener {

    }

    init {

        photoRecyclerView=findViewById(R.id.photo_recycler_view)
        gridLayoutManager= GridLayoutManager(context,3)
        photoRecyclerView.layoutManager=gridLayoutManager
        Log.d(TAG, "onCreateView: addOnGlobalLayout")
    }

}