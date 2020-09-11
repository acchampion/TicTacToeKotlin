package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.OnNetworkActiveListener
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * Fragment for Tic-Tac-Toe help.
 *
 * Created by adamcchampion on 2017/08/14.
 */

class HelpFragment : Fragment(), View.OnClickListener, OnNetworkActiveListener {

	// private static final String TAG = HelpFragment.class.getSimpleName();
	private var mIsNetActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_help, container, false)

        val wikipedia: Button = v.findViewById(R.id.button_lookup_wikipedia)
        wikipedia.setOnClickListener(this)
        val wikipediaWebView: Button = v.findViewById(R.id.button_lookup_wikipedia_in_web_view)
        wikipediaWebView.setOnClickListener(this)

        return v
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.help)
        }
		val connectivityManager = requireActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		connectivityManager.addDefaultNetworkActiveListener(this)
    }

	override fun onPause() {
		super.onPause()
		val activity: Activity = requireActivity()
		val connectivityManager = activity.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		connectivityManager.removeDefaultNetworkActiveListener(this)
	}

    private fun hasNetworkConnection(): Boolean {
        return mIsNetActive
    }

    private fun launchBrowser(url: String) {
        val theUri = Uri.parse(url)
        val launchBrowserIntent = Intent(Intent.ACTION_VIEW, theUri)
        startActivity(launchBrowserIntent)
    }

    private fun launchWebView(url: String) {
        val launchWebViewIntent = Intent(activity?.applicationContext, HelpWebViewActivity::class.java)
        launchWebViewIntent.putExtra("url", url)
        startActivity(launchWebViewIntent)
    }

    private fun noNetworkConnectionNotify() {
        val manager = parentFragmentManager
        val fragment = NoNetworkConnectionDialogFragment()
		fragment.show(manager, "no_net_conn")
    }

    override fun onClick(view: View) {
        when (view.id) {
			R.id.button_lookup_wikipedia -> if (hasNetworkConnection()) {
				launchBrowser("https://en.wikipedia.org/wiki/Tic-tac-toe")
			} else {
				noNetworkConnectionNotify()
			}
			R.id.button_lookup_wikipedia_in_web_view -> if (hasNetworkConnection()) {
				launchWebView("https://en.wikipedia.org/wiki/Tic-tac-toe")
			} else {
				noNetworkConnectionNotify()
			}
        }
    }

	override fun onNetworkActive() {
		mIsNetActive = true
	}
}
