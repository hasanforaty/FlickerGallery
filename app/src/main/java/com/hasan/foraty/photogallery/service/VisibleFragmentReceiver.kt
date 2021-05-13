package com.hasan.foraty.photogallery.service

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.impl.utils.ForceStopRunnable
import com.hasan.foraty.photogallery.data.PollWorker

abstract class VisibleFragmentReceiver :Fragment() {
    private val onShowNotification = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            resultCode = Activity.RESULT_CANCELED

        }
    }

    override fun onStart() {
        super.onStart()
        val filterIntent = IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(
            onShowNotification,
            filterIntent,
            PollWorker.PRIVATE_PERMISION,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }
}