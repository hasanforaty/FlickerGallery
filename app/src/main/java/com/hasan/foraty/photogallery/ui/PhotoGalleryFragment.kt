package com.hasan.foraty.photogallery.ui

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.data.GalleryItem
import com.hasan.foraty.photogallery.data.ThumbnailDownloader
import kotlin.math.roundToInt

private const val TAG="PhotoGalleryFragment"
class PhotoGalleryFragment :Fragment(),ViewTreeObserver.OnGlobalLayoutListener {
    private lateinit var photoRecyclerView:RecyclerView
    private lateinit var photoGalleryViewModel:PhotoGalleryViewModel
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    companion object{
        /**
         * newInstance get Instance of PhotoGalleryFragment
         * @return and instance of PhotoGalleryFragment
         */
        fun newInstance():PhotoGalleryFragment{
            return PhotoGalleryFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_photo_gallery,container,false)

        photoRecyclerView=view.findViewById(R.id.photo_recycler_view)
        gridLayoutManager=GridLayoutManager(context,3)
        photoRecyclerView.layoutManager=gridLayoutManager
        Log.d(TAG, "onCreateView: addOnGlobalLayout")
        photoRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner){galleryItems ->
                photoRecyclerView.adapter=PhotoAdapter(galleryItems)
            }

        //add an observer to our viewLifecycle from ThumbnailDownloader
        // class in onViewCreated method
        /*
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
         */
        viewLifecycleOwnerLiveData.observe(viewLifecycleOwner){viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(
                    thumbnailDownloader.viewLifecycleObserver
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //retain fragment in config changes
        retainInstance=true
        //instantiate PhotoGalleryViewModel
        photoGalleryViewModel=ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
        //instantiate ThumbnailDownloader
        val responseHandler=Handler()
        thumbnailDownloader= ThumbnailDownloader(responseHandler){ photoHolder,bitmap ->
            val drawable = BitmapDrawable(resources,bitmap)
            photoHolder.bindDrawable(drawable)
        }

        //add an observer to our fragment lifecycle from
        // ThumbnailDownloader class in onCreate Method
        lifecycle.addObserver(
            thumbnailDownloader.fragmentLifecycleObserver
        )
    }

    //ViewHolder for our Adapter
    private inner class PhotoHolder(itemImageView:ImageView):RecyclerView.ViewHolder(itemImageView){
//        val bindingTitle: (CharSequence) -> Unit =itemImageView::setContentDescription
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
    }
    //adapter for our RecyclerView
    private inner class PhotoAdapter(private val galleryItems:List<GalleryItem>):RecyclerView.Adapter<PhotoHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val imageView=layoutInflater.inflate(R.layout.list_item,parent,false)
            return PhotoHolder(imageView as ImageView)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem=galleryItems[position]
            val placeHolder: Drawable =ContextCompat
                .getDrawable(requireContext(),R.drawable.pre_pic_foreground) ?:ColorDrawable()
            holder.bindDrawable(placeHolder)
            thumbnailDownloader.queueThumbnail(holder,galleryItem.url)
        }

        override fun getItemCount(): Int {
            return galleryItems.size
        }

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

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            thumbnailDownloader.fragmentLifecycleObserver
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
    }
}
