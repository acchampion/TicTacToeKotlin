package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.HelpWebViewActivity

/**
 * Fragment for Tic-Tac-Toe help.
 *
 * Created by adamcchampion on 2017/08/14.
 */

class HelpFragment : Fragment(), View.OnClickListener {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v = inflater.inflate(R.layout.fragment_help, container, false)

		val wikipedia: Button = v.findViewById(R.id.button_wikipedia)
		wikipedia.setOnClickListener(this)
		val wikipediaWebView: Button = v.findViewById(R.id.button_wikipedia_webview)
		wikipediaWebView.setOnClickListener(this)

		return v
	}

	override fun onResume() {
		super.onResume()
		(requireActivity() as AppCompatActivity).supportActionBar?.apply {
			subtitle = resources.getString(R.string.help)
		}
	}


	private fun hasNetworkConnection(): Boolean {
		val connMgr =
			requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo = connMgr.activeNetworkInfo
		val network = connMgr.activeNetwork
		return connMgr.isDefaultNetworkActive && networkInfo != null && networkInfo.isConnected
	}

	private fun launchBrowser(url: String) {
		val theUri = Uri.parse(url)
		val launchBrowserIntent = Intent(Intent.ACTION_VIEW, theUri)
		startActivity(launchBrowserIntent)
	}

	private fun launchWebView(url: String) {
		val launchWebViewIntent =
			Intent(activity?.applicationContext, HelpWebViewActivity::class.java)
		launchWebViewIntent.putExtra("url", url)
		startActivity(launchWebViewIntent)
	}

	private fun notifyNetworkConnection() {
		val manager = parentFragmentManager
		val fragment = NoNetworkConnectionDialogFragment()
		fragment.show(manager, "no_net_conn")
	}

	override fun onClick(view: View) {
		when (view.id) {
			R.id.button_wikipedia -> if (hasNetworkConnection()) {
				launchBrowser("https://en.wikipedia.org/wiki/Tic-tac-toe")
			} else {
				notifyNetworkConnection()
			}
			R.id.button_wikipedia_webview -> if (hasNetworkConnection()) {
				launchWebView("https://en.wikipedia.org/wiki/Tic-tac-toe")
			} else {
				notifyNetworkConnection()
			}
		}
	}
}
