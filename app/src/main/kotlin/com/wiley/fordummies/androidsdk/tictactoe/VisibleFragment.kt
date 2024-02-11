package com.wiley.fordummies.androidsdk.tictactoe

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker
import timber.log.Timber

@Keep
open class VisibleFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private val mOnShowNotification: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val toastStr = "Got a broadcast: " + intent.action
            Timber.tag(TAG).i(toastStr)
            // Toast.makeText(requireContext(), toastStr, Toast.LENGTH_LONG).show();
            // If we receive this, we're visible, so cancel the notification
            Timber.tag(TAG).i("Canceling notification")
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION)
        ContextCompat.registerReceiver(
            requireContext(),
            mOnShowNotification,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(mOnShowNotification)
    }
}
