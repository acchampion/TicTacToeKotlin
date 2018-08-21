package com.wiley.fordummies.androidsdk.tictactoe

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

/**
 * Fragment for Tic-Tac-Toe help.
 *
 * Created by adamcchampion on 2017/08/14.
 */

class HelpFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_help, container, false)

        val btOK: Button = v.findViewById(R.id.button_help_ok)
        btOK.setOnClickListener(this)
        val wikipedia: Button = v.findViewById(R.id.button_lookup_wikipedia)
        wikipedia.setOnClickListener(this)
        val wikipediaWebView: Button = v.findViewById(R.id.button_lookup_wikipedia_in_web_view)
        wikipediaWebView.setOnClickListener(this)

        return v
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.help)
        }
    }

    @Suppress("DEPRECATION")
    private fun hasNetworkConnection(): Boolean {
        val connectivityManager = activity?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val isWifiAvailable = networkInfo.isAvailable
        val isWifiConnected = networkInfo.isConnected
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val isMobileAvailable = networkInfo.isAvailable
        val isMobileConnected = networkInfo.isConnected
        return (isMobileAvailable && isMobileConnected) || (isWifiAvailable && isWifiConnected)
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
        val manager = fragmentManager
        val fragment = NoNetworkConnectionDialogFragment()
        fragment.show(manager, "no_net_conn")
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_help_ok -> activity?.finishFromChild(activity)
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
}
