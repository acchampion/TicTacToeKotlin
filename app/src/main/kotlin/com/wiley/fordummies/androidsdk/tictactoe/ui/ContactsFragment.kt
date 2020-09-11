package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import com.wiley.fordummies.androidsdk.tictactoe.R
import timber.log.Timber

/**
 * Fragment for displaying contacts.
 *
 * Source: https://github.com/alfongj/android-recyclerview-contacts-example,
 * https://developer.android.com/training/contacts-provider/retrieve-names.html
 *
 * Created by adamcchampion on 2017/08/16.
 */
class ContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mContactsListView: ListView

    // An adapter that binds the result Cursor to the ListView
    private lateinit var mCursorAdapter: androidx.cursoradapter.widget.SimpleCursorAdapter

    // private val TAG = ContactsFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContactsListView = requireActivity().findViewById(R.id.contact_list_view)
        requestContacts()
    }

    override fun onResume() {
        super.onResume()
		val activity = requireActivity() as AppCompatActivity
        activity.actionBar?.apply {
            subtitle = resources.getString(R.string.contacts)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasReadContactPermission()) {
                val fm = requireActivity().supportFragmentManager
                val dialogFragment = ContactPermissionDeniedDialogFragment()
				dialogFragment.show(fm, "contact_perm_denied")
            }
        }
    }

    private fun requestContacts() {
        Timber.d("requestContacts()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasReadContactPermission()) {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSION_REQUEST_READ_CONTACTS)
            } else {
                showContacts()
            }
        } else {
            showContacts()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun hasReadContactPermission(): Boolean {
        return activity?.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts()
            } else {
                Timber.e("Error: Permission denied to read contacts")
                Toast.makeText(activity, resources.getString(R.string.read_contacts_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showContacts() {
        Timber.d("showContacts()")

        // Gets a CursorAdapter
        mCursorAdapter = androidx.cursoradapter.widget.SimpleCursorAdapter(
                activity,
                R.layout.list_item_contact, null,
                FROM_COLUMNS,
                TO_IDS,
                0)
        // Sets the adapter for the ListView
        mContactsListView.adapter = mCursorAdapter

        // Initializes the loader
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<Cursor> {
        return androidx.loader.content.CursorLoader(
                activity as Context,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        )
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, data: Cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        mCursorAdapter.swapCursor(null)
    }

    companion object {
        private val PROJECTION = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

        private const val PERMISSION_REQUEST_READ_CONTACTS = 1

        /*
         * Defines an array that contains column names to move from the Cursor to the ListView.
        */
        private val FROM_COLUMNS = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

        /*
         * Defines an array that contains resource ids for the layout views
         * that get the Cursor column contents. The id is pre-defined in
         * the Android framework, so it is prefaced with "android.R.id"
         */
        private val TO_IDS = intArrayOf(R.id.contact_info)
    }
}
