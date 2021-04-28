package com.hasan.foraty.photogallery.screens.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.data.GalleryItem
import com.hasan.foraty.photogallery.data.PollWorker
import com.hasan.foraty.photogallery.data.ThumbnailDownloader
import com.hasan.foraty.photogallery.screens.common.fragment.BaseFragment

private const val TAG="PhotoGalleryFragment"
class PhotoGalleryFragment :BaseFragment(), SearchView.OnQueryTextListener {

    private lateinit var photoGalleryViewModel:PhotoGalleryViewModel
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    private lateinit var viewMvc: PhotoGalleryMvc

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
        viewMvc=compositionRoot.viewMvc.newPhotoGalleryMvc(container)
        return viewMvc.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner){galleryItems ->
                viewMvc.photoRecyclerView.adapter=PhotoAdapter(galleryItems)
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
        //activating menu
        setHasOptionsMenu(true)
        //creating condition for our worker
        val constraint = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        //creating work Request
        val workRequest = OneTimeWorkRequest
            .Builder(PollWorker::class.java)
            .setConstraints(constraint)
            .build()
        //run the worker
        WorkManager.getInstance().enqueue(workRequest)


    }
    //inflate menu with created menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_photo_gallery_menu,menu)
        val searchView=menu.findItem(R.id.menu_search).actionView as SearchView
        //add listener to search action bar
        searchView.setOnQueryTextListener(this)
        //adding last search to search widget
        val searchText=photoGalleryViewModel.searchText
        if (searchText.isNotEmpty()){
            //make sure to don't submit , only add the text
            searchView.setQuery(searchText,false)
        }
    }



    //ViewHolder for our Adapter
    private inner class PhotoHolder(view:View):RecyclerView.ViewHolder(view){
//        val bindingTitle: (CharSequence) -> Unit =itemImageView::setContentDescription
        val  itemImageView:ImageView = view.findViewById(R.id.image)
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
    }
    //adapter for our RecyclerView
    private inner class PhotoAdapter(private val galleryItems:List<GalleryItem>):RecyclerView.Adapter<PhotoHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val cardView=layoutInflater.inflate(R.layout.list_item,parent,false)
            return PhotoHolder(cardView)
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
    //doing search for subject enter in search box
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "onQueryTextSubmit: query: $query")
        query?.let { query ->
            photoGalleryViewModel.searchPhoto(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d(TAG, "onQueryTextChange: text change to $newText")
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_clear ->{
                photoGalleryViewModel.searchPhoto("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
