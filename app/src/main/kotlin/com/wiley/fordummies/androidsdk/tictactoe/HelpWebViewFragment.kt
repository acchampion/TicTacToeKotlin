package com.wiley.fordummies.androidsdk.tictactoe

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar

/**
 * Fragment that shows WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */
@SuppressWarnings("LogNotTimber")
class HelpWebViewFragment : Fragment(), View.OnClickListener {
    private lateinit var mUrl: String
    private lateinit var mProgressBar: ProgressBar
    private val ARG_URI = "url"
    private val TAG = javaClass.simpleName

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_help_webview, container, false)

        val helpInWebView = v.findViewById<WebView>(R.id.helpwithwebview)
        mProgressBar = v.findViewById<ProgressBar>(R.id.webviewprogress)
        mProgressBar.apply {
            max = 100
        }

        val buttonExit = v.findViewById<Button>(R.id.button_exit)
        buttonExit.setOnClickListener(this)
        val extras = activity?.intent?.extras
        if (extras != null) {
            mUrl = extras.getString(ARG_URI)
            Log.d(TAG, "Loading URL " + mUrl)
        }
        WebView.setWebContentsDebuggingEnabled(true)
        helpInWebView.settings.javaScriptEnabled = true
        helpInWebView.webViewClient = object : WebViewClient() {
            @Suppress("OverridingDeprecatedMember")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }
        helpInWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView, progress: Int) {
                if (progress == 100) {
                    mProgressBar.visibility = View.GONE
                } else {
                    mProgressBar.visibility = View.VISIBLE
                    mProgressBar.progress = progress
                }
            }
        }

        helpInWebView.loadUrl(mUrl)

        return v
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.help_webview)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_exit -> activity?.finishFromChild(activity)
        }
    }
}
