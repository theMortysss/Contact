package com.example.library.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.library.R
import com.example.library.databinding.FragmentContactMapBinding
import com.example.library.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileFrag: FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileFrag = FragmentProfileBinding.bind(view)

        profileFrag.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}
