package com.example.library.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.library.R
import com.example.library.utils.Constants.TAG
import kotlinx.coroutines.*

private const val START_DELAY: Long = 2000

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val mDelayJob: CompletableJob = Job()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.READ_CONTACTS)
        } else {
            navToMain()
        }
    }
    private fun navToMain() {
        CoroutineScope(Dispatchers.Main).launch(mDelayJob) {
            delay(START_DELAY)
            findNavController().navigate(R.id.action_helloFragment_to_mainFragment)
        }
    }
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {
                Log.d(TAG, "permission granted by the user")
                findNavController().navigate(R.id.action_helloFragment_to_mainFragment)
            } else {
                Log.d(TAG, "permission denied by the user")
            }
        }
}
