package com.hasan.foraty.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hasan.foraty.photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG="PhotoGalleryFragment"
class PhotoGalleryFragment :Fragment() {
    private lateinit var photoRecyclerView:RecyclerView
    private lateinit var photoGalleryViewModel:PhotoGalleryViewModel

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
        photoRecyclerView.layoutManager=GridLayoutManager(context,3)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner){galleryItems ->
                photoRecyclerView.adapter=PhotoAdapter(galleryItems)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoGalleryViewModel=ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
    }


    private inner class PhotoHolder(itemTextView:TextView):RecyclerView.ViewHolder(itemTextView){
        val bindingTitle: (CharSequence) -> Unit =itemTextView::setText
    }
    private inner class PhotoAdapter(private val galleryItems:List<GalleryItem>):RecyclerView.Adapter<PhotoHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val textView=TextView(parent.context)
            return PhotoHolder(textView)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem=galleryItems[position]
            holder.bindingTitle(galleryItem.title)
        }

        override fun getItemCount(): Int {
            return galleryItems.size
        }

    }















}