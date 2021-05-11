package com.hasan.foraty.photogallery.ui

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import androidx.work.impl.WorkManagerInitializer
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.data.GalleryItem
import com.hasan.foraty.photogallery.data.PollWorker
import com.hasan.foraty.photogallery.data.QueryPreference
import com.hasan.foraty.photogallery.data.ThumbnailDownloader
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private const val TAG="PhotoGalleryFragment"
private const val WORKER_POLL_TAG = "Poll Work"
class PhotoGalleryFragment :Fragment(),ViewTreeObserver.OnGlobalLayoutListener, SearchView.OnQueryTextListener {
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
        //activating menu
        setHasOptionsMenu(true)



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

        val togglePolling = menu.findItem(R.id.menu_item_toggle_polling)
        val pollingStatue = QueryPreference.isPolling(requireContext())
        val pollingTitle=if (pollingStatue){
            R.string.stop_polling
        }else{
            R.string.start_polling
        }
        togglePolling.title = getString(pollingTitle)

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
            R.id.menu_item_toggle_polling -> {
                val pollStatue = QueryPreference.isPolling(requireContext())
                if (pollStatue) {
                    WorkManager.getInstance(requireContext()).cancelUniqueWork(WORKER_POLL_TAG)
                    QueryPreference.setPolling(requireContext(), false)

                } else {
                    //creating condition for our worker
                    val constraint = Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                    //creating work Request
                    val workRequest = PeriodicWorkRequest
                        .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(constraint)
                        .build()
                    //run the worker
                    WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                        WORKER_POLL_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        workRequest
                    )
                    QueryPreference.setPolling(requireContext(), true)

                }
                activity?.invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
