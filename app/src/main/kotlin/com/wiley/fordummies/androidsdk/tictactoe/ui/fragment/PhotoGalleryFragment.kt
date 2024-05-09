package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.TicTacToeApplication
import com.wiley.fordummies.androidsdk.tictactoe.VisibleFragment
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStore
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.PhotoGalleryViewModel
import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Keep
class PhotoGalleryFragment : VisibleFragment() {
	private val mPhotoGalleryViewModel: PhotoGalleryViewModel by viewModels()
	private lateinit var mDataStore: SettingsDataStore
	private lateinit var mPhotoRecyclerView: RecyclerView

	private val classTag = javaClass.simpleName
	private val pollWork = "POLL_WORK"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
		setHasOptionsMenu(true)
		mDataStore = TicTacToeApplication.getDataStore()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
		mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view)
		mPhotoRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
		return view
	}

	override fun onResume() {
		super.onResume()
		Timber.tag(classTag).d("onResume()")
		try {
			val activity = requireActivity() as AppCompatActivity
			val actionBar = activity.supportActionBar
			if (actionBar != null) {
				actionBar.subtitle = resources.getString(R.string.photo_gallery)
			}
		} catch (npe: NullPointerException) {
			Timber.tag(classTag).e("Could not set subtitle")
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		mPhotoGalleryViewModel.galleryItemLiveData.observe(viewLifecycleOwner) { galleryItems: List<GalleryItem> ->
			val adapter = PhotoAdapter(galleryItems)
			adapter.notifyDataSetChanged()
			mPhotoRecyclerView.adapter = adapter
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
	}

	override fun onDestroy() {
		super.onDestroy()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_photo_gallery, menu)
		val searchItem = menu.findItem(R.id.menu_item_search)
		val searchView = searchItem.actionView as SearchView

		searchView.apply {
			setOnQueryTextListener(object : SearchView.OnQueryTextListener {
				override fun onQueryTextSubmit(query: String): Boolean {
					Timber.tag(classTag).d("QueryTextSubmit: %s", query)
					mPhotoGalleryViewModel.fetchPhotos(query)
					return true
				}

				override fun onQueryTextChange(newText: String): Boolean {
					Timber.tag(classTag).d("QueryTextChange: %s", newText)
					return false
				}
			})

			setOnSearchClickListener {
				searchView.setQuery(mPhotoGalleryViewModel.searchTerm, false)
			}
		}

		val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
		CoroutineScope(Dispatchers.IO).launch {
			val isPolling = mDataStore.getBoolean(Settings.Keys.PREF_IS_POLLING, false)
			withContext(Dispatchers.Main) {
				val toggleItemTitle: Int = if (isPolling) {
					R.string.stop_polling
				} else {
					R.string.start_polling
				}
				toggleItem.setTitle(toggleItemTitle)
			}
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.menu_item_clear -> {
				mPhotoGalleryViewModel.fetchPhotos()
				true
			}
			R.id.menu_item_toggle_polling -> {
				CoroutineScope(Dispatchers.IO).launch {
					val isPolling = mDataStore.getBoolean(Settings.Keys.PREF_IS_POLLING, false)
					if (isPolling) {
						WorkManager.getInstance(requireContext()).cancelUniqueWork(pollWork)
						mDataStore.putBoolean(Settings.Keys.PREF_IS_POLLING, false)
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
							pollWork,
							ExistingPeriodicWorkPolicy.KEEP,
							periodicRequest
						)
						mDataStore.putBoolean(Settings.Keys.PREF_IS_POLLING, true)
					}
				}

				requireActivity().invalidateOptionsMenu()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private inner class PhotoHolder(val itemImageView: ImageView) :
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
			val view = layoutInflater.inflate(
				R.layout.list_item_gallery,
				parent,
				false
			) as ImageView
			return PhotoHolder(view)

		}

		override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
			val galleryItem = mGalleryItems[position]
			holder.bindGalleryItem(galleryItem)
			val placeholder: Drawable = ContextCompat.getDrawable(
				requireContext(),
				R.drawable.image_placeholder
			) ?: ColorDrawable()
			Glide.with(requireContext())
				.load(galleryItem.url)
				.placeholder(placeholder)
				.into(holder.itemImageView)
		}

		override fun getItemCount(): Int {
			return mGalleryItems.size
		}
	}
}
