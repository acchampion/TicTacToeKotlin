package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.model.Contact
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.ContactViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * New Fragment to show contacts in a RecyclerView.
 *
 * Created by acc on 2021/08/09.
 */
class ContactsFragment : Fragment() {
	private lateinit var mContactRecyclerView: RecyclerView
	private val mContactViewModel: ContactViewModel by viewModels()

	private var contactJob: Job? = null

	private val mActivityResult = registerForActivityResult(
		RequestPermission()
	) { result ->
		if (result) {
			// We have permission, so show the contacts.
			showContacts()
		} else {
			// The user denied permission to read contacts, so show them a message.
			Timber.e("Error: Permission denied to read contacts")
			if (lacksReadContactPermission()) {
				val activity = requireActivity() as AppCompatActivity
				val fm = activity.supportFragmentManager
				val dialogFragment = ContactPermissionDeniedDialogFragment()
				dialogFragment.show(fm, "contact_perm_denied")
			}
		}
	}

	private val classTag = javaClass.simpleName

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_contact_list, container, false)
	}

	override fun onResume() {
		super.onResume()
		try {
			val activity = requireActivity() as AppCompatActivity
			val actionBar = activity.supportActionBar
			actionBar?.subtitle = resources.getString(R.string.contacts)
			requestContacts()
		} catch (npe: NullPointerException) {
			Timber.e("Could not set subtitle")
		}
	}

	override fun onStop() {
		super.onStop()
		contactJob?.cancel()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val activity: Activity = requireActivity()
		mContactRecyclerView = view.findViewById(R.id.contact_recycler_view)
		mContactRecyclerView.layoutManager = LinearLayoutManager(activity)
	}

	private fun requestContacts() {
		Timber.d("requestContacts()")
		if (lacksReadContactPermission()) {
			mActivityResult.launch(Manifest.permission.READ_CONTACTS)
		} else {
			showContacts()
		}
	}

	private fun lacksReadContactPermission(): Boolean {
		val activity: Activity = requireActivity()
		return activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
	}

	private fun showContacts() {
		Timber.d("showContacts()")
		contactJob = viewLifecycleOwner.lifecycleScope.launch {
			val contactList = mContactViewModel.loadContacts(requireActivity().contentResolver)
			mContactViewModel.getContactsLiveData(requireActivity().applicationContext.contentResolver).observe(
				(activity as LifecycleOwner)
			) { newContactList: List<Contact?>? ->
				Timber.tag(classTag).d("List of contacts changed; %d elements", newContactList?.size)
				if (newContactList != null && !newContactList.contains(null)) {
					val theList: List<Contact> = ArrayList(newContactList.size)
					val contactAdapter = ContactAdapter(newContactList as List<Contact>)
					mContactRecyclerView.swapAdapter(contactAdapter, true)
				}
			}
		}
	}

	private class ContactHolder constructor(inflater: LayoutInflater, parent: ViewGroup?) :
		RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_contact, parent, false)) {
		private val mContactTextView: TextView = itemView.findViewById(R.id.contact_info)
		fun bind(contact: Contact) {
			val name = contact.name
			mContactTextView.text = name
		}

	}

	private inner class ContactAdapter constructor(private val mContactList: List<Contact>) :
		RecyclerView.Adapter<ContactHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
			val inflater = requireActivity().layoutInflater
			return ContactHolder(inflater, parent)
		}

		override fun onBindViewHolder(holder: ContactHolder, position: Int) {
			val contact = mContactList[position]
			holder.bind(contact)
		}

		override fun getItemCount(): Int {
			return mContactList.size
		}
	}
}

