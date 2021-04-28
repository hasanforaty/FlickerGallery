package com.hasan.foraty.photogallery.screens.photogallery

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.screens.common.viewsmvc.BaseViewMvc
import kotlin.math.roundToInt

private const val TAG = "PhotoGalleryMvc"
class PhotoGalleryMvc(
        private val layoutInflater: LayoutInflater,
        private val parent: ViewGroup?
) : BaseViewMvc<PhotoGalleryMvc.Listener> (
        layoutInflater, parent, R.layout.fragment_photo_gallery)
        ,ViewTreeObserver.OnGlobalLayoutListener {
    lateinit var photoRecyclerView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager

    interface Listener {

    }

    init {

        photoRecyclerView=findViewById(R.id.photo_recycler_view)
        gridLayoutManager= GridLayoutManager(context,3)
        photoRecyclerView.layoutManager=gridLayoutManager
        Log.d(TAG, "onCreateView: addOnGlobalLayout")
        photoRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    //dynamic chose of number of column base on Screen Size
    override fun onGlobalLayout() {
        Log.d(TAG, "onGlobalLayout: width = ${photoRecyclerView.width} , height = ${photoRecyclerView.height}")
        val width=photoRecyclerView.width
        val measure=360
        var inSize= (width / measure).toDouble().roundToInt()
        if (inSize>=3){
            inSize--
        }
        gridLayoutManager.spanCount= inSize
    }
}