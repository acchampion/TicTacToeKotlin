package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.VisibleFragment
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.PhotoGalleryViewModel
import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker
import com.wiley.fordummies.androidsdk.tictactoe.network.ThumbnailDownloader
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PhotoGalleryFragment : VisibleFragment() {
    private lateinit var mPhotoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var mPhotoRecyclerView: RecyclerView
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    private val TAG = javaClass.simpleName
    private val POLL_WORK = "POLL_WORK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        val activity: Activity = requireActivity()
        mPhotoGalleryViewModel =
			ViewModelProvider((activity as ViewModelStoreOwner)).get(
            PhotoGalleryViewModel::class.java
        )
		val responseHandler = Handler(Looper.getMainLooper())
        mThumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            val drawable = BitmapDrawable( resources, bitmap )
            photoHolder.bindDrawable(drawable)
        }
        mThumbnailDownloader.start()
        mThumbnailDownloader.looper
        lifecycle.addObserver(mThumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
		lifecycle.addObserver(mThumbnailDownloader.viewLifecycleObserver)
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view)
		mPhotoRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        return view
    }

    override fun onResume() {
        super.onResume()
        Timber.tag(TAG).d("onResume()")
        try {
            val activity = requireActivity() as AppCompatActivity
            val actionBar = activity.supportActionBar
            if (actionBar != null) {
                actionBar.subtitle = resources.getString(R.string.photo_gallery)
            }
        } catch (npe: NullPointerException) {
            Timber.tag(TAG).e("Could not set subtitle")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPhotoGalleryViewModel.galleryItemLiveData.observe(viewLifecycleOwner,
			Observer { galleryItems: List<GalleryItem> ->
                val adapter = PhotoAdapter(galleryItems)
                adapter.notifyDataSetChanged()
                mPhotoRecyclerView.adapter = adapter
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(mThumbnailDownloader.viewLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mThumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_photo_gallery, menu)
        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

		searchView.apply {
			setOnQueryTextListener(object : SearchView.OnQueryTextListener {
				override fun onQueryTextSubmit(query: String): Boolean {
					Timber.tag(TAG).d("QueryTextSubmit: %s", query)
					mPhotoGalleryViewModel.fetchPhotos(query)
					return true
				}

				override fun onQueryTextChange(newText: String): Boolean {
					Timber.tag(TAG).d("QueryTextChange: %s", newText)
					return false
				}
			})

			setOnSearchClickListener {
				searchView.setQuery(mPhotoGalleryViewModel.searchTerm, false)
			}
		}


        val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
        val isPolling = QueryPreferences.isPolling(requireContext())
		val toggleItemTitle: Int = if (isPolling) {
			R.string.stop_polling
		} else {
			R.string.start_polling
		}
        toggleItem.setTitle(toggleItemTitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.menu_item_clear -> {
				mPhotoGalleryViewModel.fetchPhotos()
				true
			}
			R.id.menu_item_toggle_polling -> {
				val isPolling = QueryPreferences.isPolling(requireContext())
				if (isPolling) {
					WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
					QueryPreferences.setPolling(requireContext(), false)
				} else {
					val constraints = Constraints.Builder()
						.setRequiredNetworkType(NetworkType.UNMETERED)
						.build()
					val periodicRequest = PeriodicWorkRequest.Builder(
						PollWorker::class.java,
						15,
						TimeUnit.MINUTES
					)
						.setConstraints(constraints)
						.build()
					WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
						POLL_WORK,
						ExistingPeriodicWorkPolicy.KEEP,
						periodicRequest
					)
					QueryPreferences.setPolling(requireContext(), true)
				}

				requireActivity().invalidateOptionsMenu()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
    }

    private inner class PhotoHolder(private val itemImageView: ImageView) :
        RecyclerView.ViewHolder(itemImageView), View.OnClickListener {
        private lateinit var mGalleryItem: GalleryItem

        init {
        	itemView.setOnClickListener(this)
        }

		val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable

        fun bindGalleryItem(item: GalleryItem) {
            mGalleryItem = item
        }

        override fun onClick(v: View) {
            CustomTabsIntent.Builder()
                .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
                .setShowTitle(true)
                .build()
				.launchUrl(requireContext(), mGalleryItem.photoPageUri)
        }
    }

    private inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
			val view = layoutInflater.inflate(R.layout.list_item_gallery,
			parent,
			false) as ImageView
			return PhotoHolder(view)

        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = mGalleryItems[position]
            holder.bindGalleryItem(galleryItem)
            val placeholder: Drawable = ContextCompat.getDrawable(
					requireContext(),
				R.drawable.image_placeholder) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }

        override fun getItemCount(): Int {
            return mGalleryItems.size
        }
    }
}
